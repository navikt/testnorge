import React from 'react'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { CheckboxGroup } from '@navikt/ds-react'

export const Attributt = ({ attr, vis = true, disabled = false, title = null, ...props }) =>
	vis && (
		<div title={title}>
			<DollyCheckbox
				label={attr.label}
				attributtCheckbox={true}
				size={'small'}
				onChange={attr.checked ? attr.remove : attr.add}
				value={attr.label}
				disabled={disabled}
				{...props}
			/>
		</div>
	)

export const AttributtKategori = ({ title, children, attr }) => {
	const kapplah = attr && Object.values(attr)
	const checked = kapplah
		?.filter((attribute) => attribute.checked)
		?.map((attribute) => attribute.label)

	const attributter = Array.isArray(children) ? children : [children]
	const attributterSomSkalVises = attributter.some(
		(attr) => attr.props.vis || !attr.props.hasOwnProperty('vis')
	)
	return (
		attributterSomSkalVises && (
			<CheckboxGroup name={title} legend={title} value={checked}>
				<div className="attributt-velger_panelsubcontent">{children}</div>
			</CheckboxGroup>
		)
	)
}
