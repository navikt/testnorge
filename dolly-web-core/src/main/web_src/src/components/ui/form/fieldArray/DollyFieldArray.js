import React from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import Button from '~/components/ui/button/Button'
import HjelpeTekst from 'nav-frontend-hjelpetekst'

import './dollyFieldArray.less'

export const FieldArrayAddButton = ({ title, onClick }) => (
	<Button
		className="flexbox--align-center"
		kind="add-circle"
		onClick={onClick}
		title={`Legg til ${title}`}
	>
		{title}
	</Button>
)

export const FieldArrayRemoveButton = ({ onClick }) => (
	<Button className="field-group-remove" kind="remove-circle" onClick={onClick} title="Fjern" />
)

export const DollyFaBlokk = ({ title, idx, arrayHelpers, hjelpetekst, children }) => {
	const handleRemove = () => arrayHelpers.remove(idx)
	if (title === 'Partner' && idx === 0) title = title.concat(' (nåværende)')
	return (
		<div className="dfa-blokk">
			<div className="dfa-blokk-header">
				<span>{idx + 1}</span>
				<h2>{title}</h2>
				{hjelpetekst && <HjelpeTekst>{hjelpetekst}</HjelpeTekst>}
				<Button kind="trashcan" onClick={handleRemove} title="Fjern" />
			</div>
			<div className="dfa-blokk-content">{children}</div>
		</div>
	)
}

export const DollyFieldArray = ({ name, title, newEntry, hjelpetekst, children }) => (
	<FieldArray name={name}>
		{arrayHelpers => {
			const values = _get(arrayHelpers.form.values, name)
			return (
				<React.Fragment>
					{values.map((curr, idx) => {
						const path = `${name}.${idx}`
						return (
							<DollyFaBlokk
								key={idx}
								idx={idx}
								title={title}
								hjelpetekst={hjelpetekst}
								arrayHelpers={arrayHelpers}
							>
								{children(path, idx, curr)}
							</DollyFaBlokk>
						)
					})}

					<FieldArrayAddButton title={title} onClick={e => arrayHelpers.push(newEntry)} />
				</React.Fragment>
			)
		}}
	</FieldArray>
)
