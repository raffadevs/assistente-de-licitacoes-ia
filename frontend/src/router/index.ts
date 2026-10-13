import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'documentos', component: () => import('../views/DocumentosView.vue') },
    {
      path: '/documentos/:id',
      name: 'documento-detalhe',
      component: () => import('../views/DocumentoDetalheView.vue'),
      props: (route) => ({ id: Number(route.params.id) }),
    },
  ],
});

export default router;
