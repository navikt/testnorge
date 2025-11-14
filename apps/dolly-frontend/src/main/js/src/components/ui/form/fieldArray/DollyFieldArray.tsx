import React, { useEffect } from 'react'
import Button from '@/components/ui/button/Button'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import ExpandableBlokk from './ExpandableBlokk'
import './dollyFieldArray.less'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import styled from 'styled-components'
import { useFieldArray, useFormContext } from 'react-hook-form'

const numberColor = {
	ARRAY_LEVEL_ONE: '#CCE3ED',
	ARRAY_LEVEL_TWO: '#FFE5C2',
	ARRAY_LEVEL_THREE: '#CDE7D8',
	ARRAY_LEVEL_FOUR: '#C1B5D0',
}

export const FieldArrayAddButton = ({
	hoverText = null,
	addEntryButtonText,
	onClick,
	disabled = false,
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

const DeleteButton = ({ onClick }) =>
	!onClick ? null : (
		<Button
			className="dolly-delete-button"
			kind="trashcan"
			fontSize="1.4rem"
			onClick={onClick}
			title="Fjern"
		/>
	)

const Numbering = ({ idx, color = numberColor.ARRAY_LEVEL_ONE }) => (
	<span className="dfa-blokk-number" style={{ backgroundColor: color }}>
		{idx}
	</span>
)

const FaError = styled.div`
	color: #ba3a26;
	font-style: italic;
	margin-bottom: 10px;
`

export const DollyFieldArrayWrapper = ({
	header = null,
	hjelpetekst = null,
	nested = false,
	children,
}) => (
	<div className="dfa">
		{nested && header && (
			<div className="dfa-blokk-nested_title">
				<h3>{header}</h3>
				{hjelpetekst && <Hjelpetekst>{hjelpetekst}</Hjelpetekst>}
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
	number,
	whiteBackground,
}) => {
	const className = whiteBackground ? 'dfa-blokk-white' : 'dfa-blokk'
	return (
		<div className={className}>
			<div className={`${className}_header`}>
				<Numbering idx={number || idx + 1} />
				<h2>{header}</h2>
				{hjelpetekst && <Hjelpetekst>{hjelpetekst}</Hjelpetekst>}
				{showDeleteButton && <DeleteButton onClick={handleRemove} />}
			</div>
			<div className={`${className}_content`}>{children}</div>
		</div>
	)
}

export const DollyFaBlokkOrg = ({
	header,
	idx,
	handleRemove,
	hjelpetekst,
	children,
	showDeleteButton,
	number,
}) => {
	const nivaa = number ? number.split('.').length : 1
	const name = nivaa % 2 === 1 ? 'dfa-blokk-org-odd' : 'dfa-blokk-org-even'
	const getNivaaColor = () => {
		switch (nivaa) {
			case 1:
				return numberColor.ARRAY_LEVEL_ONE
			case 2:
				return numberColor.ARRAY_LEVEL_TWO
			case 3:
				return numberColor.ARRAY_LEVEL_THREE
			case 4:
				return numberColor.ARRAY_LEVEL_FOUR
			default:
				return numberColor.ARRAY_LEVEL_ONE
		}
	}
	const color = getNivaaColor()
	return (
		<div className={name}>
			<div className={`${name}_header`}>
				<Numbering idx={number || idx + 1} color={color} />
				<h2>{header}</h2>
				{hjelpetekst && <Hjelpetekst>{hjelpetekst}</Hjelpetekst>}
				{showDeleteButton && <DeleteButton onClick={handleRemove} />}
			</div>
			<div className={`${name}_content`}>{children}</div>
		</div>
	)
}

export const DollyFaBlokkNested = ({
	idx,
	children,
	whiteBackground,
	handleRemove = null,
	showDeleteButton = true,
}) => (
	<div className="dfa-blokk-nested">
		<div className="dfa-blokk_header">
			<Numbering idx={idx + 1} />
		</div>
		<div className={whiteBackground ? 'dfa-blokk_content_white' : 'dfa-blokk_content'}>
			{showDeleteButton && <DeleteButton onClick={handleRemove} />}
			{children}
		</div>
	</div>
)

export const DollyFieldArray = ({
	ignoreOnSingleElement = false,
	header = null as unknown as string,
	hjelpetekst = null,
	data,
	nested = false,
	children,
	expandable = false,
	getHeader = null as unknown as Function,
	whiteBackground = false,
}) => {
	if (!data || data.length === 0 || Array.isArray(data) === false) return null
	if (ignoreOnSingleElement && data.length === 1) return children(data[0], 0)
	return (
		<ErrorBoundary>
			<DollyFieldArrayWrapper header={header} hjelpetekst={hjelpetekst} nested={nested}>
				{data.map((curr, idx) => {
					if (nested)
						return (
							<DollyFaBlokkNested key={idx} idx={idx} whiteBackground={whiteBackground}>
								{children(curr, idx)}
							</DollyFaBlokkNested>
						)
					if (expandable)
						return (
							<ExpandableBlokk
								key={idx}
								idx={idx}
								getHeader={getHeader ? getHeader : () => header}
								data={curr}
								whiteBackground={whiteBackground}
							>
								{children(curr, idx)}
							</ExpandableBlokk>
						)
					return (
						<DollyFaBlokk
							key={idx}
							idx={idx}
							header={getHeader ? getHeader(curr) : header}
							hjelpetekst={hjelpetekst}
							whiteBackground={whiteBackground}
						>
							{children(curr, idx)}
						</DollyFaBlokk>
					)
				})}
			</DollyFieldArrayWrapper>
		</ErrorBoundary>
	)
}

export const FormDollyFieldArray = ({
	name,
	title = null,
	header,
	newEntry,
	hjelpetekst = null as unknown as string,
	nested = false,
	children,
	disabled = false,
	canBeEmpty = true,
	tag = null,
	isOrganisasjon = false,
	handleNewEntry = null as any,
	handleRemoveEntry = null,
	maxEntries = null as unknown as number,
	whiteBackground = false,
	maxReachedDescription = null,
	buttonText = null as unknown as string,
	errorText = null,
	lockedEntriesLength = 0 as unknown as number,
	leafOnlyDelete = false,
}) => {
	const formMethods = useFormContext()
	const { fields, append, remove } = useFieldArray({ control: formMethods.control, name })
	const values = formMethods.watch(name) || []

	useEffect(() => {
		if (!canBeEmpty && fields.length === 0 && !(leafOnlyDelete && isOrganisasjon)) {
			append(newEntry)
			formMethods.trigger(name)
		}
	}, [canBeEmpty, fields.length, append, formMethods, name, leafOnlyDelete, isOrganisasjon])

	const addNewEntry = () => {
		if (handleNewEntry) {
			try {
				if (handleNewEntry.length >= 2) handleNewEntry(append, values)
				else if (handleNewEntry.length === 1) handleNewEntry(append)
				else handleNewEntry()
			} catch (_) {
				append(newEntry)
			}
		} else append(newEntry)
		formMethods.trigger(name)
	}

	return (
		<ErrorBoundary>
			<DollyFieldArrayWrapper header={header} hjelpetekst={hjelpetekst} nested={nested}>
				{fields.map((field, idx) => {
					const curr = values[idx]
					const path = `${name}.${idx}`
					const number = tag ? `${tag}.${idx + 1}` : `${idx + 1}`
					const isLeaf = !curr || !curr.underenheter || curr.underenheter.length === 0
					let showDeleteButton
					if (leafOnlyDelete && isOrganisasjon) {
						if (!isLeaf) showDeleteButton = false
						else
							showDeleteButton = !(values.length === 1 && !canBeEmpty && idx < lockedEntriesLength)
					} else {
						showDeleteButton = canBeEmpty ? true : values.length >= 2 && idx >= lockedEntriesLength
					}
					const handleRemove = () => {
						remove(idx)
						if (handleRemoveEntry) handleRemoveEntry(idx)
						formMethods.trigger(name)
					}
					if (nested)
						return (
							<DollyFaBlokkNested
								key={field.id}
								idx={idx}
								handleRemove={handleRemove}
								showDeleteButton={showDeleteButton}
								whiteBackground={whiteBackground}
							>
								{children(path, idx, curr, number, field.id)}
							</DollyFaBlokkNested>
						)
					if (isOrganisasjon)
						return (
							<DollyFaBlokkOrg
								key={field.id}
								idx={idx}
								number={number}
								header={header}
								hjelpetekst={hjelpetekst}
								handleRemove={showDeleteButton ? handleRemove : null}
								showDeleteButton={showDeleteButton}
							>
								{children(path, idx, curr, number, field.id)}
							</DollyFaBlokkOrg>
						)
					return (
						<DollyFaBlokk
							key={field.id}
							idx={idx}
							number={number}
							header={header}
							hjelpetekst={hjelpetekst}
							handleRemove={showDeleteButton ? handleRemove : null}
							showDeleteButton={showDeleteButton}
							whiteBackground={whiteBackground}
						>
							{children(path, idx, curr, number, field.id)}
						</DollyFaBlokk>
					)
				})}
				{errorText && <FaError>{errorText}</FaError>}
				<FieldArrayAddButton
					hoverText={title || (maxEntries === values.length && maxReachedDescription)}
					addEntryButtonText={buttonText || header}
					onClick={addNewEntry}
					disabled={disabled || maxEntries === values.length}
				/>
			</DollyFieldArrayWrapper>
		</ErrorBoundary>
	)
}
