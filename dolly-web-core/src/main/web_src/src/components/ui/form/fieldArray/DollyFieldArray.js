import React from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import Button from '~/components/ui/button/Button'
import HjelpeTekst from 'nav-frontend-hjelpetekst'

import './dollyFieldArray.less'

export const FieldArrayAddButton = ({ title, onClick }) => (
	<Button kind="add-circle" onClick={onClick} title={`Legg til ${title}`}>
		{title}
	</Button>
)

export const FieldArrayRemoveButton = ({ onClick }) => (
	<Button className="field-group-remove" kind="remove-circle" onClick={onClick} title="Fjern" />
)

const DeleteButton = ({ onClick }) => {
	if (!onClick) return null
	return <Button kind="trashcan" onClick={onClick} title="Fjern" />
}

const Numbering = ({ idx }) => <span className="dfa-blokk-number">{idx + 1}</span>

const DollyFieldArrayWrapper = ({ title, hjelpetekst, nested, children }) => (
	<div className="dfa">
		{nested && title && (
			<div className="dfa-blokk-nested_title">
				<h3>{title}</h3>
				{hjelpetekst && <HjelpeTekst>{hjelpetekst}</HjelpeTekst>}
			</div>
		)}
		{children}
	</div>
)

export const DollyFaBlokk = ({ title, idx, handleRemove, hjelpetekst, children }) => (
	<div className="dfa-blokk">
		<div className="dfa-blokk_header">
			<Numbering idx={idx} />
			<h2>{title}</h2>
			{hjelpetekst && <HjelpeTekst>{hjelpetekst}</HjelpeTekst>}
			<DeleteButton onClick={handleRemove} />
		</div>
		<div className="dfa-blokk_content">{children}</div>
	</div>
)

export const DollyFaBlokkNested = ({ idx, handleRemove, children }) => (
	<div className="dfa-blokk-nested">
		<div className="dfa-blokk_header">
			<Numbering idx={idx} />
		</div>
		<div className="dfa-blokk_content">
			<DeleteButton onClick={handleRemove} />
			{children}
		</div>
	</div>
)

export const DollyFieldArray = ({ title, hjelpetekst, data, nested = false, children }) => (
	<DollyFieldArrayWrapper title={title} hjelpetekst={hjelpetekst} nested={nested}>
		{data.map((curr, idx) => {
			return nested ? (
				<DollyFaBlokkNested key={idx} idx={idx}>
					{children(curr, idx)}
				</DollyFaBlokkNested>
			) : (
				<DollyFaBlokk key={idx} idx={idx} title={title} hjelpetekst={hjelpetekst}>
					{children(curr, idx)}
				</DollyFaBlokk>
			)
		})}
	</DollyFieldArrayWrapper>
)

export const FormikDollyFieldArray = ({
	name,
	title,
	newEntry,
	hjelpetekst,
	nested = false,
	children
}) => (
	<FieldArray name={name}>
		{arrayHelpers => {
			const values = _get(arrayHelpers.form.values, name, [])
			const addNewEntry = () => arrayHelpers.push(newEntry)
			return (
				<DollyFieldArrayWrapper title={title} hjelpetekst={hjelpetekst} nested={nested}>
					{values.map((curr, idx) => {
						const path = `${name}.${idx}`
						const handleRemove = () => arrayHelpers.remove(idx)
						return nested ? (
							<DollyFaBlokkNested key={idx} idx={idx} handleRemove={handleRemove}>
								{children(path, idx, curr)}
							</DollyFaBlokkNested>
						) : (
							<DollyFaBlokk
								key={idx}
								idx={idx}
								title={title}
								hjelpetekst={hjelpetekst}
								handleRemove={handleRemove}
							>
								{children(path, idx, curr)}
							</DollyFaBlokk>
						)
					})}

					<FieldArrayAddButton title={title} onClick={addNewEntry} />
				</DollyFieldArrayWrapper>
			)
		}}
	</FieldArray>
)
