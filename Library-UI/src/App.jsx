/* eslint-disable no-unused-vars */
import React, { useState, useEffect, useCallback } from 'react';
import axios from 'axios';

const API_BASE = "http://localhost:8080/api/books";

export default function App() {
  const [books, setBooks] = useState([]);
  const [authors, setAuthors] = useState([]);
  const [publishers, setPublishers] = useState([]);
  const [query, setQuery] = useState('');
  const [activeTab, setActiveTab] = useState('all');
  
  
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [bookToDelete, setBookToDelete] = useState(null);

  
  const loadData = useCallback(async (endpoint = "", type = "books") => {
    try {
      const res = await axios.get(`${API_BASE}${endpoint}`);
      if (type === "books") setBooks(res.data);
      if (type === "authors") setAuthors(res.data);
      if (type === "publishers") setPublishers(res.data);
      setCurrentPage(1); // Sekme değişince 1. sayfaya dön
    } catch (err) {
      console.error("Hata:", err.message);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

 
  const getCurrentItems = () => {
    const data = activeTab === 'authors' ? authors : activeTab === 'publishers' ? publishers : books;
    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    return data.slice(indexOfFirstItem, indexOfLastItem);
  };

  const totalPages = Math.ceil(
    (activeTab === 'authors' ? authors.length : activeTab === 'publishers' ? publishers.length : books.length) / itemsPerPage
  );

  
  const confirmDelete = async () => {
    if (!bookToDelete) return;
    try {
      await axios.delete(`${API_BASE}/${bookToDelete.bookID}`);
      setIsModalOpen(false);
      setBookToDelete(null);
      loadData(); 
    } catch (err) { alert("Silme hatası!"); }
  };

  const handleFetch = async () => {
    if (!query) return;
    try {
      await axios.get(`${API_BASE}/fetch?q=${encodeURIComponent(query)}`);
      setQuery('');
      loadData();
      setActiveTab('all');
    } catch (err) { alert("Bağlantı hatası!"); }
  };

  return (
    <div className="min-h-screen bg-slate-50 p-6 md:p-12 font-sans text-slate-900">
      <div className="max-w-6xl mx-auto">
        
        {/* HEADER & SEARCH */}
        <header className="flex flex-col md:flex-row justify-between items-center mb-10 gap-6">
          <h1 className="text-4xl font-black text-indigo-700 tracking-tighter italic uppercase underline decoration-indigo-200">Library Hub</h1>
          <div className="flex shadow-lg rounded-2xl overflow-hidden bg-white border border-indigo-100 w-full md:w-auto">
            <input 
              className="px-6 py-3 flex-1 md:w-80 outline-none font-medium text-slate-700"
              placeholder="Google Books'ta ara..."
              value={query} onChange={(e) => setQuery(e.target.value)}
            />
            <button onClick={handleFetch} className="bg-indigo-600 hover:bg-indigo-700 text-white px-8 font-bold transition-all">FETCH</button>
          </div>
        </header>

        
        <nav className="flex flex-wrap gap-3 mb-8 overflow-x-auto pb-2 scrollbar-hide">
          <TabBtn active={activeTab==='all'} label="Tüm Kitaplar" onClick={() => {setActiveTab('all'); loadData();}} />
          <TabBtn active={activeTab==='authors'} label="Yazarlar" onClick={() => {setActiveTab('authors'); loadData('/authors', 'authors');}} />
          <TabBtn active={activeTab==='publishers'} label="Yayınevleri" onClick={() => {setActiveTab('publishers'); loadData('/publishers', 'publishers');}} />
          <TabBtn active={activeTab==='startA'} label="'A' Harfi ile Başlayanlar" onClick={() => {setActiveTab('startA'); loadData('/starts-with-a');}} />
          <TabBtn active={activeTab==='recent'} label="2023 Sonrası Basımlar" onClick={() => {setActiveTab('recent'); loadData('/recent');}} />
        </nav>

        {/* MODERN TABLO */}
        <div className="bg-white rounded-[2.5rem] shadow-2xl overflow-hidden border border-slate-100 mb-8">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-indigo-600 text-white uppercase text-[10px] tracking-widest font-black">
                <th className="px-10 py-6 text-center">No</th>
                {activeTab === 'authors' ? (
                  <th className="px-10 py-6">Yazar Adı Soyadı</th>
                ) : activeTab === 'publishers' ? (
                  <th className="px-10 py-6">Yayınevi Adı</th>
                ) : (
                  <>
                    <th className="px-10 py-6">Kitap Başlığı</th>
                    <th className="px-10 py-6">Yazar</th>
                    <th className="px-10 py-6">Yayınevi</th>
                    <th className="px-10 py-6 text-right">İşlemler</th>
                  </>
                )}
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {getCurrentItems().map((item, idx) => (
                <tr key={idx} className="hover:bg-indigo-50/30 transition-colors group">
                  <td className="px-10 py-5 text-center">
                    <IDBadge id={activeTab==='authors' ? item.authorID : activeTab==='publishers' ? item.publisherID : item.bookID} />
                  </td>
                  {activeTab === 'authors' ? (
                    <td className="px-10 py-5 font-bold uppercase text-slate-700">{item.authorNameSurname}</td>
                  ) : activeTab === 'publishers' ? (
                    <td className="px-10 py-5 font-bold uppercase text-slate-700">{item.publisherName}</td>
                  ) : (
                    <>
                      <td className="px-10 py-6 font-black text-slate-800 uppercase text-sm tracking-tighter leading-none">{item.title}</td>
                      <td className="px-10 py-6 text-slate-500 italic font-medium text-sm">{item.authorNameSurname}</td>
                      <td className="px-10 py-6 text-indigo-600 font-bold text-xs">{item.publisherName}</td>
                      <td className="px-10 py-6 text-right">
                       
                        <button 
                          onClick={() => { setBookToDelete(item); setIsModalOpen(true); }}
                          className="text-red-400 hover:text-red-600 transition-all p-2 bg-red-50/0 hover:bg-red-50 rounded-xl"
                        >
                          <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      </td>
                    </>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* 2) SAYFALAMA KONTROLLERİ (10'ar ayırma) */}
        <div className="flex justify-center gap-2 mb-10">
          <button 
            disabled={currentPage === 1}
            onClick={() => setCurrentPage(p => p - 1)}
            className="px-4 py-2 bg-white rounded-xl border border-slate-200 disabled:opacity-30 font-black text-xs text-indigo-600"
          >
            ← ÖNCEKİ
          </button>
          <span className="px-6 py-2 bg-indigo-600 text-white rounded-xl font-black text-xs flex items-center">
            SAYFA {currentPage} / {totalPages || 1}
          </span>
          <button 
            disabled={currentPage === totalPages || totalPages === 0}
            onClick={() => setCurrentPage(p => p + 1)}
            className="px-4 py-2 bg-white rounded-xl border border-slate-200 disabled:opacity-30 font-black text-xs text-indigo-600"
          >
            SONRAKİ →
          </button>
        </div>
      </div>

      
      {isModalOpen && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-[3rem] p-10 max-w-sm w-full shadow-2xl animate-in zoom-in duration-300">
            <div className="text-center">
              <div className="w-20 h-20 bg-red-50 text-red-600 rounded-full flex items-center justify-center mx-auto mb-6 animate-pulse">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-10 w-10" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
              </div>
              <h2 className="text-2xl font-black text-slate-800 mb-2 italic">KAYDI SİL?</h2>
              <p className="text-slate-500 text-sm mb-8 font-medium italic">"<span className="text-red-600 font-bold not-italic">{bookToDelete?.title}</span>" kalıcı olarak silinecektir.</p>
              <div className="flex gap-4">
                <button onClick={() => setIsModalOpen(false)} className="flex-1 px-6 py-4 rounded-2xl bg-slate-100 text-slate-500 font-black hover:bg-slate-200 transition-all uppercase text-[10px]">İPTAL</button>
                <button onClick={confirmDelete} className="flex-1 px-6 py-4 rounded-2xl bg-red-600 text-white font-black hover:bg-red-700 shadow-xl transition-all uppercase text-[10px]">EVET, SİL</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}


const IDBadge = ({id}) => (
  <span className="inline-flex items-center justify-center px-3 py-1 bg-indigo-50 text-indigo-600 rounded-lg text-[10px] font-black border border-indigo-100 shadow-sm min-w-[40px]">{id}</span>
);

const TabBtn = ({label, active, onClick}) => (
  <button onClick={onClick} className={`px-6 py-3 rounded-2xl text-[10px] font-black transition-all ${active ? 'bg-indigo-600 text-white shadow-xl scale-105' : 'bg-white text-slate-400 border border-slate-200 hover:bg-indigo-50'}`}>{label}</button>
);