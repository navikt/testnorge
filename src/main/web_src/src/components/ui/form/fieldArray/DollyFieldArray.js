import React from 'react'
import { FieldArray } from 'formik'
import _get from 'lodash/get'
import Button from '~/components/ui/button/Button'
import Hjelpetekst from '~/components/hjelpetekst'
import ExpandableBlokk from './ExpandableBlokk'

import './dollyFieldArray.less'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const FieldArrayAddButton = ({
	hoverText = null,
	addEntryButtonText,
	onClick,
	disabled = false
}) => (
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

const Numbering = ({ idx }) => <span className="dfa-blokk-number">{idx}</span>

export const DollyFieldArrayWrapper = ({
	header = null,
	hjelpetekst = null,
	nested = false,
	children
}) => (
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

export const DollyFaBlokk = ({
	header,
	idx,
	handleRemove,
	hjelpetekst,
	children,
	showDeleteButton,
	number
}) => (
	<div className="dfa-blokk">
		<div className="dfa-blokk_header">
			<Numbering idx={number || idx + 1} />
			<h2>{header}</h2>
			{hjelpetekst && <Hjelpetekst hjelpetekstFor={header}>{hjelpetekst}</Hjelpetekst>}
			{showDeleteButton && <DeleteButton onClick={handleRemove} />}
		</div>
		<div className="dfa-blokk_content">{children}</div>
	</div>
)

export const DollyFaBlokkNested = ({ idx, handleRemove, children }) => (
	<div className="dfa-blokk-nested">
		<div className="dfa-blokk_header">
			<Numbering idx={idx + 1} />
		</div>
		<div className="dfa-blokk_content">
			<DeleteButton onClick={handleRemove} />
			{children}
		</div>
	</div>
)

export const DollyFieldArray = ({
	header = null,
	hjelpetekst = null,
	data,
	nested = false,
	children,
	expandable = false,
	getHeader = null
}) => (
	<ErrorBoundary>
		<DollyFieldArrayWrapper header={header} hjelpetekst={hjelpetekst} nested={nested}>
			{data.map((curr, idx) => {
				return nested ? (
					<DollyFaBlokkNested key={idx} idx={idx}>
						{children(curr, idx)}
					</DollyFaBlokkNested>
				) : expandable ? (
					<ExpandableBlokk
						key={idx}
						idx={idx}
						getHeader={getHeader ? getHeader : () => header}
						data={curr}
					>
						{children(curr, idx)}
					</ExpandableBlokk>
				) : (
					<DollyFaBlokk key={idx} idx={idx} header={header} hjelpetekst={hjelpetekst}>
						{children(curr, idx)}
					</DollyFaBlokk>
				)
			})}
		</DollyFieldArrayWrapper>
	</ErrorBoundary>
)

export const FormikDollyFieldArray = ({
	name,
	title = null,
	header,
	newEntry,
	hjelpetekst = null,
	nested = false,
	children,
	isFull = false,
	canBeEmpty = true,
	tag
}) => (
	<FieldArray name={name}>
		{arrayHelpers => {
			const values = _get(arrayHelpers.form.values, name, [])
			const addNewEntry = () => arrayHelpers.push(newEntry)
			return (
				<ErrorBoundary>
					<DollyFieldArrayWrapper header={header} hjelpetekst={hjelpetekst} nested={nested}>
						{values.map((curr, idx) => {
							const showDeleteButton = canBeEmpty === true ? true : values.length >= 2
							const path = `${name}.${idx}`
							const number = tag ? `${tag}.${idx + 1}` : `${idx + 1}`
							const handleRemove = () => arrayHelpers.remove(idx)
							return nested ? (
								<DollyFaBlokkNested key={idx} idx={idx} handleRemove={handleRemove}>
									{children(path, idx, curr)}
								</DollyFaBlokkNested>
							) : (
								<DollyFaBlokk
									key={idx}
									idx={idx}
									number={number}
									header={header}
									hjelpetekst={hjelpetekst}
									handleRemove={handleRemove}
									showDeleteButton={showDeleteButton}
								>
									{children(path, idx, curr, number)}
								</DollyFaBlokk>
							)
						})}
						<FieldArrayAddButton
							hoverText={title}
							addEntryButtonText={header}
							onClick={addNewEntry}
							disabled={isFull}
						/>
					</DollyFieldArrayWrapper>
				</ErrorBoundary>
			)
		}}
	</FieldArray>
)
