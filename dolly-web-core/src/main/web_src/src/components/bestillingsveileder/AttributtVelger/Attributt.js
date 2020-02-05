import React from 'react'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

export const Attributt = ({ attr, vis = true, disabled = false }) => {
	return (
		vis && (
			<DollyCheckbox
				label={attr.label}
				size="grow"
				onChange={attr.checked ? attr.remove : attr.add}
				checked={attr.checked}
				disabled={disabled}
			/>
		)
	)
}

export const AttributtKategori = ({ title, children }) => {
	return (
		<React.Fragment>
			{title && <h3>{title}</h3>}
			<div className="attributt-velger_panelsubcontent">{children}</div>
		</React.Fragment>
	)
}
