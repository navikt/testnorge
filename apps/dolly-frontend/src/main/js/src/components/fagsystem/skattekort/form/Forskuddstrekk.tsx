import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	DollyFaBlokk,
	DollyFieldArrayWrapper,
	FieldArrayAddButton,
} from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { useSkattekortKodeverk } from '@/utils/hooks/useSkattekort'
import { toTitleCase } from '@/utils/DataFormatter'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { useFieldArray } from 'react-hook-form'
import { UseFormReturn } from 'react-hook-form/dist/types'

type ForskuddstrekkFormProps = {
	formMethods: UseFormReturn
	path: string
}

type ForskuddstrekkProps = {
	path: string
	trekkode: any
}

const initialFrikort = {
	frikort: {
		trekkode: '',
		frikortbeloep: null,
	},
}

export const initialTrekktabell = {
	trekktabell: {
		trekkode: '',
		tabellnummer: '',
		prosentsats: null,
		antallMaanederForTrekk: null,
	},
}

const initialTrekkprosent = {
	trekkprosent: {
		trekkode: '',
		prosentsats: null,
		antallMaanederForTrekk: null,
	},
}

const FrikortForm = ({ path, trekkode }: ForskuddstrekkProps) => {
	return (
		<>
			<FormSelect
				name={`${path}.frikort.trekkode`}
				label="Trekkode"
				options={trekkode}
				size="xlarge"
				isClearable={false}
			/>
			<FormTextInput name={`${path}.frikort.frikortbeloep`} label="Frikortbeløp" type="number" />
		</>
	)
}

const TrekktabellForm = ({ path, trekkode }: ForskuddstrekkProps) => {
	return (
		<>
			<FormSelect
				name={`${path}.trekktabell.trekkode`}
				label="Trekkode"
				options={trekkode}
				size="xlarge"
				isClearable={false}
			/>
			<FormTextInput name={`${path}.trekktabell.tabellnummer`} label="Tabellnummer" type="number" />
			<FormTextInput name={`${path}.trekktabell.prosentsats`} label="Prosentsats" type="number" />
			<FormTextInput
				name={`${path}.trekktabell.antallMaanederForTrekk`}
				label="Antall måneder for trekk"
				type="number"
			/>
		</>
	)
}

const TrekkprosentForm = ({ path, trekkode }: ForskuddstrekkProps) => {
	return (
		<>
			<FormSelect
				name={`${path}.trekkprosent.trekkode`}
				label="Trekkode"
				options={trekkode}
				size="xlarge"
				isClearable={false}
			/>
			<FormTextInput name={`${path}.trekkprosent.prosentsats`} label="Prosentsats" type="number" />
			<FormTextInput
				name={`${path}.trekkprosent.antallMaanederForTrekk`}
				label="Antall måneder for trekk"
				type="number"
			/>
		</>
	)
}

export const ForskuddstrekkForm = ({ formMethods, path }: ForskuddstrekkFormProps) => {
	const forskuddstrekkPath = `${path}.forskuddstrekk`
	const forskuddstrekk = formMethods.watch(forskuddstrekkPath)
	const fieldMethods = useFieldArray({ control: formMethods.control, name: forskuddstrekkPath })

	const addNewFrikort = () => fieldMethods.append(initialFrikort)
	const addNewTrekktabell = () => fieldMethods.append(initialTrekktabell)
	const addNewTrekkprosent = () => fieldMethods.append(initialTrekkprosent)

	const { kodeverk: trekkode } = useSkattekortKodeverk('TREKKODE')

	return (
		<ErrorBoundary>
			<Kategori title="Forskuddstrekk">
				<DollyFieldArrayWrapper>
					{forskuddstrekk.map((trekk: any, idx: number) => {
						const header = Object.keys(trekk)?.[0]
						const clickRemove = () => fieldMethods.remove(idx)

						return (
							<DollyFaBlokk
								key={header + idx}
								idx={idx}
								header={toTitleCase(header)}
								handleRemove={clickRemove}
								whiteBackground
								showDeleteButton
							>
								{header === 'frikort' && (
									<FrikortForm path={`${path}.forskuddstrekk[${idx}]`} trekkode={trekkode} />
								)}
								{header === 'trekktabell' && (
									<TrekktabellForm path={`${path}.forskuddstrekk[${idx}]`} trekkode={trekkode} />
								)}
								{header === 'trekkprosent' && (
									<TrekkprosentForm path={`${path}.forskuddstrekk[${idx}]`} trekkode={trekkode} />
								)}
							</DollyFaBlokk>
						)
					})}
					<div className="flexbox--flex-wrap">
						<FieldArrayAddButton addEntryButtonText="Trekktabell" onClick={addNewTrekktabell} />
						<FieldArrayAddButton addEntryButtonText="Trekkprosent" onClick={addNewTrekkprosent} />
						<FieldArrayAddButton addEntryButtonText="Frikort" onClick={addNewFrikort} />
					</div>
				</DollyFieldArrayWrapper>
			</Kategori>
		</ErrorBoundary>
	)
}
