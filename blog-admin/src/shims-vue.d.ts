declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE: string
  readonly VITE_BLOG_FRONT: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
