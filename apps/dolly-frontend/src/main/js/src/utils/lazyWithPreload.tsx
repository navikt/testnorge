import React, { lazy, LazyExoticComponent } from 'react'
import { handleChunkErrorWithReload, isChunkLoadError } from '@/utils/chunkErrorUtils'

type PreloadableComponent<T extends React.ComponentType<any>> = LazyExoticComponent<T> & {
	preload: () => void
}

export const retryImport = <T,>(factory: () => Promise<T>): Promise<T> =>
	factory().catch((error: unknown) => {
		if (isChunkLoadError(error)) {
			handleChunkErrorWithReload()
		}
		throw error
	})

export function lazyWithPreload<T extends React.ComponentType<any>>(
	factory: () => Promise<{ default: T }>,
): PreloadableComponent<T> {
	const Component = lazy(() => retryImport(factory)) as PreloadableComponent<T>
	Component.preload = factory
	return Component
}
