import React, { useEffect, useRef } from 'react'
import { List, type RowComponentProps } from 'react-window'

const OPTION_HEIGHT = 40
const ROWS = 6

type MenuListProps = {
	options: unknown[]
	children: React.ReactNode | React.ReactNode[]
	getValue: () => unknown[]
}

type RowProps = {
	nodes: React.ReactNode[]
}

function Row({ index, style, nodes }: RowComponentProps<RowProps>) {
	return <div style={style}>{nodes[index]}</div>
}

interface ListInstance {
	scrollTo: (offset: number) => void
}

const MenuList: React.FC<MenuListProps> = ({ options, children, getValue }) => {
	const [value] = getValue()
	const nodes: React.ReactNode[] = Array.isArray(children) ? children : [children]

	const selectedIndex = options.indexOf(value)
	const rowCount = nodes.length

	if (!Array.isArray(children) || rowCount <= 1) {
		return <div>{children}</div>
	}

	const viewportHeight = rowCount >= ROWS ? ROWS * OPTION_HEIGHT : rowCount * OPTION_HEIGHT

	const listRef = useRef<ListInstance | null>(null)

	useEffect(() => {
		if (listRef.current && selectedIndex > -1 && rowCount >= ROWS) {
			const scrollTop = Math.max(0, (selectedIndex - (ROWS - 1)) * OPTION_HEIGHT)
			listRef.current.scrollTo(scrollTop)
		}
	}, [selectedIndex, rowCount])

	return (
		<div style={{ height: viewportHeight, width: '100%' }}>
			<List
				ref={listRef as any}
				rowComponent={Row}
				rowCount={rowCount}
				rowHeight={OPTION_HEIGHT}
				rowProps={{ nodes }}
			/>
		</div>
	)
}

export default MenuList
