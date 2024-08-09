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

const initialFrikort = {
	frikort: {
		trekkode: '',
		frikortbeloep: null,
	},
}

export const initialTrekktabell = {
	trekktabell: {
		trekkode: '',
		tabelltype: '',
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

const FrikortForm = ({ path, trekkode }) => {
	return (
		<>
			<FormSelect
				name={`${path}.frikort.trekkode`}
				label="Trekkode"
				options={trekkode}
				size="large"
			/>
			<FormTextInput name={`${path}.frikort.frikortbeloep`} label="Frikortbeløp" type="number" />
		</>
	)
}

const TrekktabellForm = ({ path, trekkode }) => {
	const { kodeverk: tabelltype } = useSkattekortKodeverk('TABELLTYPE')

	return (
		<>
			<FormSelect
				name={`${path}.trekktabell.trekkode`}
				label="Trekkode"
				options={trekkode}
				size="large"
			/>
			<FormSelect
				name={`${path}.trekktabell.tabelltype`}
				label="Tabelltype"
				options={tabelltype}
				size="large"
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

const TrekkprosentForm = ({ path, trekkode }) => {
	return (
		<>
			<FormSelect
				name={`${path}.trekkprosent.trekkode`}
				label="Trekkode"
				options={trekkode}
				size="large"
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

export const ForskuddstrekkForm = ({ formMethods, path }) => {
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
					{forskuddstrekk.map((trekk, idx) => {
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
