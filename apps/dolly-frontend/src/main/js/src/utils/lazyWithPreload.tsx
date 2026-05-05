import React, { lazy, LazyExoticComponent } from 'react'

type PreloadableComponent<T extends React.ComponentType<any>> = LazyExoticComponent<T> & {
	preload: () => void
}

export function lazyWithPreload<T extends React.ComponentType<any>>(
	factory: () => Promise<{ default: T }>,
): PreloadableComponent<T> {
	const Component = lazy(factory) as PreloadableComponent<T>
	Component.preload = factory
	return Component
}
