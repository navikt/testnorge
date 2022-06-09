import React from 'react'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { CheckboxGroup } from '@navikt/ds-react'

export const Attributt = ({ attr, vis = true, disabled = false, title = null }) => {
	return (
		vis && (
			<div title={title}>
				<DollyCheckbox
					label={attr.label}
					size="grow"
					attributtCheckbox={true}
					onChange={attr.checked ? attr.remove : attr.add}
					checked={attr.checked}
					disabled={disabled}
				/>
			</div>
		)
	)
}

export const AttributtKategori = ({ title, children }) => {
	const attributter = Array.isArray(children) ? children : [children]
	const attributterSomSkalVises = attributter.some(
		(attr) => attr.props.vis || !attr.props.hasOwnProperty('vis')
	)
	return (
		attributterSomSkalVises && (
			<CheckboxGroup>
				{title && <h3>{title}</h3>}
				<div className="attributt-velger_panelsubcontent">{children}</div>
			</CheckboxGroup>
		)
	)
}
