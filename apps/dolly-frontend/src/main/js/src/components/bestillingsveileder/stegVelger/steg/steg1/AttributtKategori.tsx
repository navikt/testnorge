import React, { useCallback, useMemo } from 'react'
import { CheckboxGroup } from '@navikt/ds-react'
import { AttributeItem, Attributt, AttributtProps } from './Attributt'

interface AttributtKategoriProps {
	title: string
	attr: Record<string, AttributeItem>
	children: React.ReactNode
}

export const AttributtKategori: React.FC<AttributtKategoriProps> = ({ title, attr, children }) => {
	const items = useMemo(() => Object.values(attr ?? {}), [attr])
	const selected = useMemo(() => items.filter((i) => i.checked).map((i) => i.label), [items])

	const handleGroupChange = useCallback(
		(next: string[]) => {
			const nextSet = new Set(next)
			items.forEach((i) => {
				const shouldBe = nextSet.has(i.label)
				if (shouldBe && !i.checked) i.add()
				else if (!shouldBe && i.checked) i.remove()
			})
		},
		[items],
	)

	const rendered = React.Children.toArray(children)
		.filter(Boolean)
		.map((child) => {
			if (!React.isValidElement<AttributtProps>(child)) return null
			const label = child.props.item?.label
			const item = items.find((i) => i.label === label)
			if (!item || child.props.vis === false) return null
			return React.cloneElement(child, {
				item,
				onToggle: undefined, // let group control
			})
		})
		.filter(Boolean)

	if (rendered.length === 0) return null

	return (
		<CheckboxGroup legend={title} value={selected} onChange={handleGroupChange}>
			<div className="attributt-velger_panelsubcontent">{rendered}</div>
		</CheckboxGroup>
	)
}
