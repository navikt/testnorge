import { CheckboxGroup } from '@navikt/ds-react'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import React from 'react'

interface AttrItem {
	label: string
	readonly checked: boolean
	add: () => void
	remove: () => void
}
interface AttributtProps {
	attr: AttrItem
	vis?: boolean
	disabled?: boolean
	title?: string
	id?: string
	infoTekst?: string
	[key: string]: any
}
export const Attributt: React.FC<AttributtProps> = ({
	attr,
	vis = true,
	disabled = false,
	title,
	id,
	infoTekst = '',
	...props
}) => {
	if (!vis) return null
	return (
		<div title={title} style={{ display: 'flex', alignItems: 'center' }}>
			<DollyCheckbox
				wrapperSize="tight"
				label={attr.label}
				attributtCheckbox
				size="small"
				onChange={(e) => (e.target.checked ? attr.add() : attr.remove())}
				value={attr.label}
				disabled={disabled}
				id={id}
				{...props}
			/>
			{infoTekst && <Hjelpetekst>{infoTekst}</Hjelpetekst>}
		</div>
	)
}

interface AttributtKategoriProps {
	title: string
	children: React.ReactNode
	attr: Record<string, AttrItem>
}
export const AttributtKategori: React.FC<AttributtKategoriProps> = ({ title, children, attr }) => {
	'use no memo' // Skip compilation for this component
	const values = attr && Object.values(attr)
	const checkedValues = values?.filter((a) => a.checked)?.map((a) => a.label) || []

	const attributter = Array.isArray(children) ? children : [children]
	const showAny = attributter.some(
		(child: any) => child?.props?.vis || !child?.props?.hasOwnProperty('vis'),
	)
	if (!showAny) return null

	return (
		<CheckboxGroup name={title} legend={title} value={checkedValues}>
			<div className="attributt-velger_panelsubcontent">{children}</div>
		</CheckboxGroup>
	)
}
