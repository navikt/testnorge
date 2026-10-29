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
import React, { useMemo, useState } from 'react'
import { InnsettelseForm } from '@/components/fagsystem/kdi/form/partials/InnsettelseForm'
import { LoeslatelseForm } from '@/components/fagsystem/kdi/form/partials/LoeslatelseForm'
import { AvbruddStartForm } from '@/components/fagsystem/kdi/form/partials/AvbruddStartForm'
import { AvbruddSluttForm } from '@/components/fagsystem/kdi/form/partials/AvbruddSluttForm'
import { ForventetLoeslatelseForm } from '@/components/fagsystem/kdi/form/partials/ForventetLoeslatelseForm'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import {
	DollyFaBlokk,
	DollyFieldArrayWrapper,
	FieldArrayAddButton,
} from '@/components/ui/form/fieldArray/DollyFieldArray'
import { validation } from '@/components/fagsystem/kdi/form/validation'
import { useFengsel } from '@/utils/hooks/useInstitusjon'
import { RedigeringAnnulleringForm } from '@/components/fagsystem/kdi/form/partials/RedigeringAnnulleringForm'
import { Tag } from '@navikt/ds-react'
import styled from 'styled-components'
import { KdiMeldingProps } from '@/components/fagsystem/kdi/form/partials/types'

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

type Meldingstype = (typeof meldingstyper)[number]

type Melding = KdiMeldingProps & {
	key: Meldingstype['key']
	header: Meldingstype['header']
	Form: Meldingstype['Form']
	idx: number
}

const meldingType = {
	ANNULLERT: 'Annullert',
	REDIGERING: 'Redigering',
}

const FieldArrayTag = ({ type }: { type: any }) => (
	<Tag
		variant="outline"
		size="small"
		data-color={type === meldingType.ANNULLERT ? 'danger' : 'meta-purple'}
		style={{ marginLeft: '10px' }}
	>
		{type}
	</Tag>
)

const MeldingGruppe = styled.div`
	background-color: var(--ax-bg-meta-purple-soft);
	border-left: 4px solid var(--ax-border-meta-purple);
	padding-top: 10px;
	margin-bottom: 10px;

	&& .dfa-blokk {
		background-color: transparent;
		border-left: none;
	}
`

export const naaPubliseringstidspunkt = () =>
	Temporal.Now.plainDateTimeISO()
		.round({ smallestUnit: 'second', roundingMode: 'trunc' })
		.toString()

export const publiseringstidspunktTid = (publiseringstidspunkt: unknown) =>
	publiseringstidspunkt ? new Date(publiseringstidspunkt as string).getTime() : Infinity

export const KdiForm = () => {
	const formMethods = useFormContext()
	const annulleringer = formMethods.watch('instdataKdi.annullering') || []

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

	const [sortVersjon, setSortVersjon] = useState(0)

	// Rekkefoelgen fryses mens publiseringstidspunkt redigeres, og oppdateres kun naar
	// brukeren trykker "Oppdater rekkefoelge", naar meldinger legges til/fjernes, eller ved ny render.
	const meldingGrupper = useMemo(() => {
		const grupper = new Map<string, Array<Melding>>()

		meldingstyper.forEach(({ key, header, Form }) =>
			fieldArrays[key].fields.forEach((_field: any, idx: number) => {
				const melding = {
					...formMethods.getValues(`instdataKdi.${key}[${idx}]`),
					key,
					header,
					Form,
					idx,
				}
				const gruppeKey = melding.hendelseId ? `${key}-${melding.hendelseId}` : `${key}-${idx}`
				grupper.set(gruppeKey, [...(grupper.get(gruppeKey) || []), melding])
			}),
		)

		const sisteTidspunkt = (gruppeMeldinger: Array<Melding>) => {
			const redigeringer = gruppeMeldinger.filter((melding) => !melding.meldingId)
			const relevanteMeldinger = redigeringer.length > 0 ? redigeringer : gruppeMeldinger
			return publiseringstidspunktTid(
				relevanteMeldinger[relevanteMeldinger.length - 1].publiseringstidspunkt,
			)
		}

		return Array.from(grupper, ([gruppeKey, gruppeMeldinger]) => ({
			gruppeKey,
			meldinger: gruppeMeldinger.sort(
				(a, b) =>
					publiseringstidspunktTid(a.publiseringstidspunkt) -
					publiseringstidspunktTid(b.publiseringstidspunkt),
			),
		})).sort((a, b) => sisteTidspunkt(a.meldinger) - sisteTidspunkt(b.meldinger))
	}, [
		sortVersjon,
		fieldArrays.innsettelse.fields,
		fieldArrays.loeslatelse.fields,
		fieldArrays.avbruddStart.fields,
		fieldArrays.avbruddSlutt.fields,
		fieldArrays.forventetLoeslatelse.fields,
	])

	const antallMeldinger = meldingGrupper.reduce(
		(antall, gruppe) => antall + gruppe.meldinger.length,
		0,
	)

	const { fengsler } = useFengsel()
	const fengselOptions = fengsler
		? Object.entries(fengsler).map(([key, value]) => ({
				value: key,
				label: `${key} - ${value}`,
			}))
		: []

	const handleRedigering = (key, initialValues) => {
		const redigertTidspunkt =
			Temporal.PlainDateTime.from(initialValues.publiseringstidspunkt)
				?.add({ seconds: 1 })
				?.toString() || naaPubliseringstidspunkt()

		fieldArrays[key].append({
			...initialValues,
			meldingId: null,
			publiseringstidspunkt: redigertTidspunkt,
		})
	}

	const renderMelding = ({ key, header, Form, idx }: Melding, meldingNr: number) => {
		const hendelse = formMethods.getValues(`instdataKdi.${key}[${idx}]`)
		const meldingId = formMethods.getValues(`instdataKdi.${key}[${idx}].meldingId`)
		const hendelseId = formMethods.getValues(`instdataKdi.${key}[${idx}].hendelseId`)

		const erEksisterendeMelding = !!meldingId
		const erRedigering = !erEksisterendeMelding && !!hendelseId
		const harAnnullering = annulleringer?.find((a) => a.annullertMeldingId === meldingId)

		return (
			<DollyFaBlokk
				key={`${key}-${idx}`}
				idx={idx}
				number={meldingNr}
				header={header}
				handleRemove={() => fieldArrays[key].remove(idx)}
				tag={
					harAnnullering ? (
						<FieldArrayTag type={meldingType.ANNULLERT} />
					) : erRedigering ? (
						<FieldArrayTag type={meldingType.REDIGERING} />
					) : null
				}
				showDeleteButton={!erEksisterendeMelding && antallMeldinger > 1}
			>
				<Form
					path={`instdataKdi.${key}[${idx}]`}
					formMethods={formMethods}
					erEksisterendeMelding={erEksisterendeMelding}
					fengselOptions={fengselOptions}
					onSort={() => setSortVersjon((versjon) => versjon + 1)}
					sortVersjon={sortVersjon}
				/>
				{erEksisterendeMelding && (
					<RedigeringAnnulleringForm
						meldingId={meldingId}
						handleRedigering={() => handleRedigering(key, hendelse)}
					/>
				)}
			</DollyFaBlokk>
		)
	}

	let meldingNr = 0

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
							{meldingGrupper.map(({ gruppeKey, meldinger }) => {
								if (meldinger.length === 1) {
									return renderMelding(meldinger[0], ++meldingNr)
								}
								return (
									<MeldingGruppe
										key={gruppeKey}
										role="group"
										aria-label={`${meldinger[0].header} med redigeringer`}
									>
										{meldinger.map((melding) => renderMelding(melding, ++meldingNr))}
									</MeldingGruppe>
								)
							})}
							<div className="flexbox--flex-wrap">
								{meldingstyper.map(({ key, header, initialValues }) => (
									<FieldArrayAddButton
										key={key}
										addEntryButtonText={header}
										onClick={() =>
											fieldArrays[key].append({
												...initialValues,
												publiseringstidspunkt: naaPubliseringstidspunkt(),
											})
										}
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
