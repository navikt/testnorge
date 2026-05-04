import React, { lazy, LazyExoticComponent } from 'react'
import { handleChunkErrorWithReload, isChunkLoadError } from '@/utils/chunkErrorUtils'

type PreloadableComponent<T extends React.ComponentType<any>> = LazyExoticComponent<T> & {
	preload: () => void
}

export const retryImport = <T,>(
	factory: () => Promise<T>,
	retries = 2,
	delay = 1000,
): Promise<T> =>
	factory().catch((error: unknown) => {
		if (retries <= 0) {
			if (isChunkLoadError(error)) {
				handleChunkErrorWithReload()
			}
			throw error
		}
		return new Promise<T>((resolve) => setTimeout(resolve, delay)).then(() =>
			retryImport(factory, retries - 1, delay * 2),
		)
	})

export function lazyWithPreload<T extends React.ComponentType<any>>(
	factory: () => Promise<{ default: T }>,
): PreloadableComponent<T> {
	const Component = lazy(() => retryImport(factory)) as PreloadableComponent<T>
	Component.preload = factory
	return Component
}
