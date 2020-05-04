import React from 'react'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const Attributt = ({ attr, vis = true, disabled = false, title = null }) => {
	return (
		vis && (
			<div title={title}>
				<DollyCheckbox
					label={attr.label}
					size="grow"
					onChange={attr.checked ? attr.remove : attr.add}
					checked={attr.checked}
					disabled={disabled}
				/>
			</div>
		)
	)
}

export const AttributtKategori = ({ title, children }) => {
	const attributterSomSkalVises = children.some(
		child => child.props.vis || !child.props.hasOwnProperty('vis')
	)
	return (
		attributterSomSkalVises && (
			<React.Fragment>
				{title && <h3>{title}</h3>}
				<div className="attributt-velger_panelsubcontent">{children}</div>
			</React.Fragment>
		)
	)
}
