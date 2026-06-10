import { useFieldArray, useFormContext } from 'react-hook-form'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import {
	initialAvbruddSlutt,
	initialAvbruddStart,
	initialForventetLoeslatelse,
	initialInnsettelse,
	initialLoeslatelse,
	instdataKdiAttributt,
} from '@/components/fagsystem/kdi/initialValues'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, usePanelError } from '@/components/ui/form/formUtils'
import React from 'react'
import { InnsettelseForm } from '@/components/fagsystem/kdi/form/partials/InnsettelseForm'
import { LoeslatelseForm } from '@/components/fagsystem/kdi/form/partials/LoeslatelseForm'
import { AvbruddStartForm } from '@/components/fagsystem/kdi/form/partials/AvbruddStartForm'
import { AvbruddSluttForm } from '@/components/fagsystem/kdi/form/partials/AvbruddSluttForm'
import { ForventetLoeslatelseForm } from '@/components/fagsystem/kdi/form/partials/ForventetLoeslatelseForm'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import {
	DollyFaBlokk,
	DollyFaBlokkNested,
	DollyFieldArrayWrapper,
	FieldArrayAddButton,
} from '@/components/ui/form/fieldArray/DollyFieldArray'
import { validation } from '@/components/fagsystem/kdi/form/validation'
import { useFengsel } from '@/utils/hooks/useInstitusjon'
import { RedigeringAnnulleringForm } from '@/components/fagsystem/kdi/form/partials/RedigeringAnnulleringForm'

const meldingstyper = [
	{
		key: 'innsettelse',
		header: 'Innsettelse',
		initialValues: initialInnsettelse,
		Form: InnsettelseForm,
	},
	{
		key: 'loeslatelse',
		header: 'Løslatelse',
		initialValues: initialLoeslatelse,
		Form: LoeslatelseForm,
	},
	{
		key: 'avbruddStart',
		header: 'Avbrudd start',
		initialValues: initialAvbruddStart,
		Form: AvbruddStartForm,
	},
	{
		key: 'avbruddSlutt',
		header: 'Avbrudd slutt',
		initialValues: initialAvbruddSlutt,
		Form: AvbruddSluttForm,
	},
	{
		key: 'forventetLoeslatelse',
		header: 'Forventet løslatelse',
		initialValues: initialForventetLoeslatelse,
		Form: ForventetLoeslatelseForm,
	},
] as const

export const KdiForm = () => {
	const formMethods = useFormContext()

	const fieldArrays = {
		innsettelse: useFieldArray({ control: formMethods.control, name: 'instdataKdi.innsettelse' }),
		loeslatelse: useFieldArray({ control: formMethods.control, name: 'instdataKdi.loeslatelse' }),
		avbruddStart: useFieldArray({ control: formMethods.control, name: 'instdataKdi.avbruddStart' }),
		avbruddSlutt: useFieldArray({ control: formMethods.control, name: 'instdataKdi.avbruddSlutt' }),
		forventetLoeslatelse: useFieldArray({
			control: formMethods.control,
			name: 'instdataKdi.forventetLoeslatelse',
		}),
	}

	// TODO: Sorteres etter rekkefoelge lagt til, evt. etter dato
	const meldinger = meldingstyper.flatMap(({ key, header, Form }) =>
		fieldArrays[key].fields.map((field, idx) => ({ key, header, Form, fieldId: field.id, idx })),
	)

	const { fengsler, loading, error } = useFengsel()
	const fengselOptions = fengsler
		? Object.entries(fengsler).map(([key, value]) => ({
				value: key,
				label: `${key} - ${value}`,
			}))
		: []

	const handleRedigering = (key, initialValues) => {
		fieldArrays[key].append({
			...initialValues,
			meldingId: null,
			publiseringstidspunkt: new Date(),
		})
	}

	return (
		<Vis attributt={instdataKdiAttributt}>
			<Panel
				heading={'KDI-meldinger'}
				startOpen={erForsteEllerTest(formMethods.getValues(), [instdataKdiAttributt])}
				hasErrors={usePanelError(instdataKdiAttributt)}
				iconType="institusjon"
			>
				<ErrorBoundary>
					<Kategori title={'KDI-meldinger'}>
						<DollyFieldArrayWrapper>
							{meldinger.map(({ key, header, Form, fieldId, idx }, meldingNr) => {
								const hendelse = formMethods.getValues(`instdataKdi.${key}[${idx}]`)
								const meldingId = formMethods.getValues(`instdataKdi.${key}[${idx}].meldingId`)
								const erEksisterendeMelding = !!meldingId

								return (
									<DollyFaBlokk
										key={fieldId}
										idx={idx}
										number={meldingNr + 1}
										header={header}
										handleRemove={() => fieldArrays[key].remove(idx)}
										// whiteBackground
										showDeleteButton={!erEksisterendeMelding}
									>
										<Form
											path={`instdataKdi.${key}[${idx}]`}
											erEksisterendeMelding={erEksisterendeMelding}
											fengselOptions={fengselOptions}
										/>
										{erEksisterendeMelding && (
											<RedigeringAnnulleringForm
												meldingId={meldingId}
												handleRedigering={() => handleRedigering(key, hendelse)}
											/>
										)}
									</DollyFaBlokk>
								)
							})}
							<div className="flexbox--flex-wrap">
								{meldingstyper.map(({ key, header, initialValues }) => (
									<FieldArrayAddButton
										key={key}
										addEntryButtonText={header}
										onClick={() => fieldArrays[key].append(initialValues)}
									/>
								))}
							</div>
						</DollyFieldArrayWrapper>
					</Kategori>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}

KdiForm.validation = validation
