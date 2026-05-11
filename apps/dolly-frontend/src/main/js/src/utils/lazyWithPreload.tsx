import React, { lazy, LazyExoticComponent } from 'react'

type PreloadableComponent<T extends React.ComponentType<any>> = LazyExoticComponent<T> & {
	preload: () => void
}

export function retryImport<T>(
	factory: () => Promise<T>,
	retries = 3,
	baseDelay = 1000,
): Promise<T> {
	return factory().catch((error) => {
		if (retries <= 0) throw error
		return new Promise<T>((resolve) => {
			setTimeout(() => {
				resolve(retryImport(factory, retries - 1, baseDelay * 2))
			}, baseDelay)
		})
	})
}

export function lazyWithPreload<T extends React.ComponentType<any>>(
	factory: () => Promise<{ default: T }>,
): PreloadableComponent<T> {
	const Component = lazy(factory) as PreloadableComponent<T>
	Component.preload = factory
	return Component
}
