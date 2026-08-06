import { Children, createContext, isValidElement, ReactNode } from 'react'

export type PanelFilterContextType = {
	filterText: string
}

export const PanelFilterContext = createContext<PanelFilterContextType>({ filterText: '' })

export const matchesFilter = (label?: string, filterText?: string): boolean =>
	!filterText || (label?.toLowerCase().includes(filterText.toLowerCase()) ?? false)

export const countMatchingAttributter = (nodes: ReactNode, filterText: string): number => {
	let count = 0
	Children.forEach(nodes, (child) => {
		if (!isValidElement(child)) {
			return
		}
		const childProps: any = child.props
		const label = childProps?.attr?.label
		if (typeof label === 'string') {
			const visible = childProps?.hasOwnProperty?.('vis') ? childProps.vis : true
			if (visible && matchesFilter(label, filterText)) {
				count++
			}
		}
		if (childProps?.children) {
			count += countMatchingAttributter(childProps.children, filterText)
		}
	})
	return count
}

export const containsCheckedAttributt = (nodes: ReactNode): boolean => {
	let found = false
	Children.forEach(nodes, (child) => {
		if (found || !isValidElement(child)) {
			return
		}
		const childProps: any = child.props
		if (childProps?.attr?.checked) {
			found = true
			return
		}
		if (childProps?.children && containsCheckedAttributt(childProps.children)) {
			found = true
		}
	})
	return found
}
