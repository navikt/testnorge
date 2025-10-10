(function(){
  function renameServices(){var link=document.querySelector('.navbar .navbar-link');if(link && link.textContent.trim()==='Services') link.textContent='Applikasjoner'}
  function normalizeId(v){return v?v.replace(/^#/,''):''}
  function headingNodes(){return Array.from(document.querySelectorAll('.doc h2[id], .doc h3[id], .doc h4[id], .doc h5[id]')).filter(h=>h.id)}
  function expandAncestors(li){for(var k=li;k;){if(!k.classList.contains('is-active')) k.classList.add('is-active');k=k.parentElement?k.parentElement.closest('.nav-item'):null}}
  function collapseOthers(li){var keep=new Set();for(var k=li;k;){keep.add(k);k=k.parentElement?k.parentElement.closest('.nav-item'):null}document.querySelectorAll('.nav .nav-item[data-depth]').forEach(function(item){var d=parseInt(item.getAttribute('data-depth'),10);if(d>3 && !keep.has(item)) item.classList.remove('is-active')})}
  function findNavLink(id){if(!id) return null;return document.querySelector('.nav .nav-link[href="#'+id+'"]')}
  var lastId=null, lastLi=null, lastLink=null
  function setCurrent(li,link){if(lastLi && lastLi!==li) lastLi.classList.remove('is-current-page'); if(li && !li.classList.contains('is-current-page')) li.classList.add('is-current-page'); lastLi=li; if(lastLink && lastLink!==link) lastLink.removeAttribute('data-active'); if(link) link.setAttribute('data-active','true'); lastLink=link}
  function activate(id,force){if(!id) return; if(!force && id===lastId) return; var link=findNavLink(id); if(!link) return; var li=link.closest('.nav-item'); setCurrent(li,link); if(li){expandAncestors(li);collapseOthers(li)} lastId=id; if(history.replaceState) history.replaceState(null,'','#'+id)}
  function initialId(){var hash=normalizeId(location.hash); if(hash) return hash; var first=document.querySelector('.doc h2[id]'); return first?first.id:''}
  function expandBase(){document.querySelectorAll('.nav .nav-item[data-depth]').forEach(function(item){var d=parseInt(item.getAttribute('data-depth'),10); if(d<=3) item.classList.add('is-active')})}
  var heads=[]
  function refreshHeads(){heads=headingNodes()}
  function pickActive(){var offset=180; var chosen=null; for(var i=0;i<heads.length;i++){var h=heads[i]; var top=h.getBoundingClientRect().top; if(top-offset<=0) chosen=h; else break} if(!chosen && heads.length) chosen=heads[0]; if(chosen) activate(chosen.id)}
  var ticking=false
  function onScroll(){if(ticking) return; ticking=true; requestAnimationFrame(function(){ticking=false; pickActive()})}
  function onClicks(){document.querySelectorAll('.nav .nav-link[href^="#"]').forEach(function(a){a.addEventListener('click',function(e){var id=normalizeId(a.getAttribute('href')); if(!id) return; var target=document.getElementById(id); if(!target) return; e.preventDefault(); activate(id,true); target.scrollIntoView({behavior:'smooth',block:'start'})})})}
  function watchMutations(){var article=document.querySelector('.doc'); if(!article) return; var mo=new MutationObserver(function(muts){var changed=false; muts.forEach(function(m){if(m.addedNodes.length||m.removedNodes.length) changed=true}); if(changed){refreshHeads(); pickActive()}}); mo.observe(article,{childList:true,subtree:true})}
  function run(){renameServices(); expandBase(); refreshHeads(); activate(initialId(),true); pickActive(); onClicks(); window.addEventListener('scroll',onScroll,{passive:true}); window.addEventListener('hashchange',function(){activate(normalizeId(location.hash),true)}); watchMutations()}
  if(document.readyState==='loading') document.addEventListener('DOMContentLoaded',run); else run();
})();
