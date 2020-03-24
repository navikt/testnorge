import React from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import Button from '~/components/ui/button/Button'
import Hjelpetekst from '~/components/hjelpetekst'

import './dollyFieldArray.less'

export const FieldArrayAddButton = ({ hoverText, addEntryButtonText, onClick, disabled }) => (
	<Button
		kind="add-circle"
		onClick={onClick}
		title={hoverText || `Legg til ${addEntryButtonText.toLowerCase()}`}
		disabled={disabled}
	>
		{addEntryButtonText}
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

export const DollyFieldArrayWrapper = ({ header, hjelpetekst, nested, children }) => (
	<div className="dfa">
		{nested && header && (
			<div className="dfa-blokk-nested_title">
				<h3>{header}</h3>
				{hjelpetekst && <Hjelpetekst hjelpetekstFor={header}>{hjelpetekst}</Hjelpetekst>}
			</div>
		)}
		{children}
	</div>
)

export const DollyFaBlokk = ({ header, idx, handleRemove, hjelpetekst, children }) => (
	<div className="dfa-blokk">
		<div className="dfa-blokk_header">
			<Numbering idx={idx} />
			<h2>{header}</h2>
			{hjelpetekst && <Hjelpetekst hjelpetekstFor={header}>{hjelpetekst}</Hjelpetekst>}
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

export const DollyFieldArray = ({ header, hjelpetekst, data, nested = false, children }) => (
	<DollyFieldArrayWrapper header={header} hjelpetekst={hjelpetekst} nested={nested}>
		{data.map((curr, idx) => {
			return nested ? (
				<DollyFaBlokkNested key={idx} idx={idx}>
					{children(curr, idx)}
				</DollyFaBlokkNested>
			) : (
				<DollyFaBlokk key={idx} idx={idx} header={header} hjelpetekst={hjelpetekst}>
					{children(curr, idx)}
				</DollyFaBlokk>
			)
		})}
	</DollyFieldArrayWrapper>
)

export const FormikDollyFieldArray = ({
	name,
	title,
	header,
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
				<DollyFieldArrayWrapper header={header} hjelpetekst={hjelpetekst} nested={nested}>
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
								header={header}
								hjelpetekst={hjelpetekst}
								handleRemove={handleRemove}
							>
								{children(path, idx, curr)}
							</DollyFaBlokk>
						)
					})}
					<FieldArrayAddButton
						hoverText={title}
						addEntryButtonText={header}
						onClick={addNewEntry}
					/>
				</DollyFieldArrayWrapper>
			)
		}}
	</FieldArray>
)
