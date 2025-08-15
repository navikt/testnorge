// Attributt.tsx
import React from 'react'
import { Checkbox } from '@navikt/ds-react'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

export interface AttributeItem {
	label: string
	checked: boolean
	add: () => void
	remove: () => void
	[key: string]: unknown
}

export interface AttributtProps {
	item: AttributeItem
	vis?: boolean
	disabled?: boolean
	infoTekst?: string
	onToggle?: (nextChecked: boolean, item: AttributeItem) => void
}

export const Attributt: React.FC<AttributtProps> = ({
	item,
	vis = true,
	disabled,
	infoTekst,
	onToggle,
}) => {
	if (!vis) return null
	const standalone = !!onToggle
	return (
		<div className="attributt-velger_panel-content">
			<Checkbox
				size="small"
				value={item.label}
				disabled={disabled}
				{...(standalone && {
					checked: item.checked,
					onChange: (e: React.ChangeEvent<HTMLInputElement>) => onToggle?.(e.target.checked, item),
				})}
			>
				{item.label}
			</Checkbox>
			{infoTekst && <Hjelpetekst>{infoTekst}</Hjelpetekst>}
		</div>
	)
}
