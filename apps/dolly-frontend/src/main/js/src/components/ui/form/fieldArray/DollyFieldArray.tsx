import { FieldArray } from 'formik'
import * as _ from 'lodash-es'
import Button from '@/components/ui/button/Button'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import ExpandableBlokk from './ExpandableBlokk'

import './dollyFieldArray.less'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import styled from 'styled-components'

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

const DeleteButton = ({ onClick }) => {
	if (!onClick) {
		return null
	}
	return <Button kind="trashcan" fontSize={'1.4rem'} onClick={onClick} title="Fjern" />
}

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
}) => (
	<div className="dfa-blokk">
		<div className="dfa-blokk_header">
			<Numbering idx={number || idx + 1} />
			<h2>{header}</h2>
			{hjelpetekst && <Hjelpetekst>{hjelpetekst}</Hjelpetekst>}
			{showDeleteButton && <DeleteButton onClick={handleRemove} />}
		</div>
		<div className="dfa-blokk_content">{children}</div>
	</div>
)

export const DollyFaBlokkOrg = ({
	header,
	idx,
	handleRemove,
	hjelpetekst,
	children,
	showDeleteButton,
	number,
}) => {
	const nivaa = (number.match(/\@/g) || []).length + 1
	const name = nivaa & 1 ? 'dfa-blokk-org-odd' : 'dfa-blokk-org-even'
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
	handleRemove,
	children,
	whiteBackground,
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
	header = null,
	hjelpetekst = null,
	data,
	nested = false,
	children,
	expandable = false,
	getHeader = null,
	whiteBackground = false,
}) => {
	if (ignoreOnSingleElement && data.length === 1) {
		return children(data[0], 0)
	}

	return (
		<ErrorBoundary>
			<DollyFieldArrayWrapper header={header} hjelpetekst={hjelpetekst} nested={nested}>
				{data.map((curr, idx) => {
					if (nested) {
						return (
							<DollyFaBlokkNested key={idx} idx={idx} whiteBackground={whiteBackground}>
								{children(curr, idx)}
							</DollyFaBlokkNested>
						)
					} else if (expandable) {
						return (
							<ExpandableBlokk
								key={idx}
								idx={idx}
								getHeader={getHeader ? getHeader : () => header}
								data={curr}
							>
								{children(curr, idx)}
							</ExpandableBlokk>
						)
					} else {
						return (
							<DollyFaBlokk
								key={idx}
								idx={idx}
								header={getHeader ? getHeader(curr) : header}
								hjelpetekst={hjelpetekst}
							>
								{children(curr, idx)}
							</DollyFaBlokk>
						)
					}
				})}
			</DollyFieldArrayWrapper>
		</ErrorBoundary>
	)
}

export const FormikDollyFieldArray = ({
	name,
	title = null,
	header,
	newEntry,
	hjelpetekst = null,
	nested = false,
	children,
	disabled = false,
	canBeEmpty = true,
	tag = null,
	isOrganisasjon = false,
	handleNewEntry = null,
	handleRemoveEntry = null,
	maxEntries = null as unknown as number,
	maxReachedDescription = null,
	buttonText = null,
	errorText = null,
}) => (
	<FieldArray name={name}>
		{(arrayHelpers) => {
			const values = _.get(arrayHelpers.form.values, name, [])
			const addNewEntry = () => {
				handleNewEntry ? handleNewEntry() : arrayHelpers.push(newEntry)
			}

			return (
				<ErrorBoundary>
					<DollyFieldArrayWrapper header={header} hjelpetekst={hjelpetekst} nested={nested}>
						{values.map((curr, idx) => {
							const showDeleteButton = canBeEmpty ? true : values.length >= 2
							const path = `${name}.${idx}`
							const number = tag ? `${tag}.${idx + 1}` : `${idx + 1}`
							const handleRemove = () => {
								handleRemoveEntry ? handleRemoveEntry(idx) : arrayHelpers.remove(idx)
							}

							if (nested) {
								return (
									<DollyFaBlokkNested
										key={idx}
										idx={idx}
										handleRemove={handleRemove}
										showDeleteButton={showDeleteButton}
									>
										{children(path, idx, curr)}
									</DollyFaBlokkNested>
								)
							} else if (isOrganisasjon) {
								return (
									<DollyFaBlokkOrg
										key={idx}
										idx={idx}
										number={number}
										header={header}
										hjelpetekst={hjelpetekst}
										handleRemove={handleRemove}
										showDeleteButton={showDeleteButton}
									>
										{children(path, idx, curr, number)}
									</DollyFaBlokkOrg>
								)
							} else {
								return (
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
							}
						})}
						{errorText && <FaError>{errorText}</FaError>}
						<FieldArrayAddButton
							hoverText={title || (maxEntries === values.length && maxReachedDescription)}
							addEntryButtonText={buttonText ? buttonText : header}
							onClick={addNewEntry}
							disabled={disabled || maxEntries === values.length}
						/>
					</DollyFieldArrayWrapper>
				</ErrorBoundary>
			)
		}}
	</FieldArray>
)
