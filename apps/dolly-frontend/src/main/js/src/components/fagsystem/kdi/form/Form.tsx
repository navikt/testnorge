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
import React, { ReactNode, useMemo, useState } from 'react'
import { InnsettelseForm } from '@/components/fagsystem/kdi/form/partials/InnsettelseForm'
import { LoeslatelseForm } from '@/components/fagsystem/kdi/form/partials/LoeslatelseForm'
import { AvbruddStartForm } from '@/components/fagsystem/kdi/form/partials/AvbruddStartForm'
import { AvbruddSluttForm } from '@/components/fagsystem/kdi/form/partials/AvbruddSluttForm'
import { ForventetLoeslatelseForm } from '@/components/fagsystem/kdi/form/partials/ForventetLoeslatelseForm'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	DollyFaBlokkNested,
	DollyFieldArrayWrapper,
	FieldArrayAddButton,
} from '@/components/ui/form/fieldArray/DollyFieldArray'
import { validation } from '@/components/fagsystem/kdi/form/validation'
import { useFengsel } from '@/utils/hooks/useInstitusjon'
import {
	AnnulleringForm,
	RedigeringKnapp,
} from '@/components/fagsystem/kdi/form/partials/RedigeringAnnulleringForm'
import { Detail, Heading, HStack, Tag } from '@navikt/ds-react'
import { KdiMeldingProps } from '@/components/fagsystem/kdi/form/partials/types'
import { HendelseKort, HendelseStatus } from '@/components/fagsystem/kdi/form/partials/HendelseKort'
import { formatDateTimeWithSeconds } from '@/utils/DataFormatter'

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
	fieldId: string
}

type Hendelse = {
	gruppeKey: string
	meldinger: Array<Melding>
}

export const naaPubliseringstidspunkt = () =>
	Temporal.Now.plainDateTimeISO()
		.round({ smallestUnit: 'second', roundingMode: 'trunc' })
		.toString()

export const publiseringstidspunktTid = (publiseringstidspunkt: unknown) =>
	publiseringstidspunkt ? new Date(publiseringstidspunkt as string).getTime() : Infinity

const etterPubliseringstidspunkt = (publiseringstidspunkt?: unknown) =>
	publiseringstidspunkt
		? Temporal.PlainDateTime.from(publiseringstidspunkt as string)
				.add({ seconds: 1 })
				.toString()
		: naaPubliseringstidspunkt()

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
	const hendelser: Array<Hendelse> = useMemo(() => {
		const grupper = new Map<string, Array<Melding>>()

		meldingstyper.forEach(({ key, header, Form }) =>
			fieldArrays[key].fields.forEach((field, idx) => {
				const melding = {
					...formMethods.getValues(`instdataKdi.${key}[${idx}]`),
					key,
					header,
					Form,
					idx,
					fieldId: field.key,
				}
				const gruppeKey = melding.hendelseId
					? `${key}-${melding.hendelseId}`
					: `${key}-${field.key}`
				grupper.set(gruppeKey, [...(grupper.get(gruppeKey) || []), melding])
			}),
		)

		const sisteTidspunkt = (gruppeMeldinger: Array<Melding>) =>
			publiseringstidspunktTid(gruppeMeldinger[gruppeMeldinger.length - 1].publiseringstidspunkt)

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

	const antallMeldinger = hendelser.reduce(
		(antall, hendelse) => antall + hendelse.meldinger.length,
		0,
	)

	const erAnnullert = (meldingId?: string) =>
		!!meldingId &&
		annulleringer.some(
			(annullering: KdiMeldingProps) => annullering.annullertMeldingId === meldingId,
		)

	const { fengsler } = useFengsel()
	const fengselOptions = fengsler
		? Object.entries(fengsler).map(([key, value]) => ({
				value: key,
				label: `${key} - ${value}`,
			}))
		: []

	const nyHendelseTidspunkt = () => {
		const sisteMelding = hendelser
			.flatMap((hendelse) => hendelse.meldinger)
			.map(({ key, idx }) =>
				formMethods.getValues(`instdataKdi.${key}[${idx}].publiseringstidspunkt`),
			)
			.filter(Boolean)
			.sort((a, b) => publiseringstidspunktTid(b) - publiseringstidspunktTid(a))[0]

		const naa = naaPubliseringstidspunkt()
		if (!sisteMelding || publiseringstidspunktTid(naa) > publiseringstidspunktTid(sisteMelding)) {
			return naa
		}

		return etterPubliseringstidspunkt(sisteMelding)
	}

	const handleRedigering = (sisteMelding: Melding) => {
		const { key, idx } = sisteMelding
		const verdier = formMethods.getValues(`instdataKdi.${key}[${idx}]`)
		const redigertTidspunkt = etterPubliseringstidspunkt(verdier.publiseringstidspunkt)

		fieldArrays[key].append({
			...verdier,
			meldingId: null,
			publiseringstidspunkt: redigertTidspunkt,
		})
	}

	const renderFelter = ({ key, Form, idx, meldingId }: Melding) => (
		<div className="flexbox--flex-wrap">
			<Form
				path={`instdataKdi.${key}[${idx}]`}
				formMethods={formMethods}
				erEksisterendeMelding={!!meldingId}
				fengselOptions={fengselOptions}
				onSort={() => setSortVersjon((versjon) => versjon + 1)}
				sortVersjon={sortVersjon}
			/>
		</div>
	)

	const renderMeldingTittel = (tittel: string, tag: ReactNode, tidspunkt?: string) => (
		<HStack
			gap="space-8"
			align="center"
			className="flexbox--full-width"
			style={{ marginBottom: '10px' }}
		>
			<Heading size="xsmall" level="4">
				{tittel}
			</Heading>
			{tag}
			{tidspunkt && <Detail textColor="subtle">{tidspunkt}</Detail>}
		</HStack>
	)

	const renderHendelseMelding = (melding: Melding, nestetIdx: number, harRedigering: boolean) => {
		const { key, idx, meldingId, fieldId } = melding

		if (!meldingId) {
			return (
				<DollyFaBlokkNested
					key={fieldId}
					idx={nestetIdx}
					handleRemove={() => fieldArrays[key].remove(idx)}
					whiteBackground
				>
					{renderMeldingTittel(
						'Redigering',
						<Tag variant="outline" size="xsmall" data-color="meta-purple">
							Ny
						</Tag>,
					)}
					{renderFelter(melding)}
				</DollyFaBlokkNested>
			)
		}

		return (
			<DollyFaBlokkNested
				key={fieldId}
				idx={nestetIdx}
				showDeleteButton={false}
				whiteBackground={harRedigering}
			>
				{renderMeldingTittel(
					'Eksisterende',
					erAnnullert(meldingId) && (
						<Tag variant="outline" size="xsmall" data-color="danger">
							Annullert
						</Tag>
					),
				)}
				{renderFelter(melding)}
				<AnnulleringForm meldingId={meldingId} />
			</DollyFaBlokkNested>
		)
	}

	const renderHendelse = ({ gruppeKey, meldinger }: Hendelse) => {
		const { key, idx, header, hendelseId } = meldinger[0]
		const eksisterendeMeldinger = meldinger.filter((melding) => !!melding.meldingId)
		const sisteEksisterendeMelding = eksisterendeMeldinger[eksisterendeMeldinger.length - 1]

		const status: HendelseStatus = {
			erNy: !hendelseId,
			harRedigering: meldinger.some((melding) => !melding.meldingId && !!melding.hendelseId),
			erAnnullert: meldinger.every((melding) => erAnnullert(melding.meldingId)),
		}

		if (!sisteEksisterendeMelding) {
			return (
				<HendelseKort
					key={gruppeKey}
					header={header}
					status={status}
					kanLukkes={false}
					startAapen
					handleRemove={antallMeldinger > 1 ? () => fieldArrays[key].remove(idx) : undefined}
				>
					{renderFelter(meldinger[0])}
				</HendelseKort>
			)
		}

		const sistePubliseringstidspunkt = formatDateTimeWithSeconds(
			sisteEksisterendeMelding.publiseringstidspunkt,
		)

		const beskrivelse = [
			`${meldinger.length} ${meldinger.length === 1 ? 'melding' : 'meldinger'}`,
			`sist publisert ${sistePubliseringstidspunkt}`,
		].join(' · ')

		return (
			<HendelseKort
				key={gruppeKey}
				header={header}
				status={status}
				beskrivelse={beskrivelse}
				kanLukkes
				startAapen={status.harRedigering}
			>
				<DollyFieldArrayWrapper nested>
					{meldinger.map((melding, nestetIdx) =>
						renderHendelseMelding(melding, nestetIdx, status.harRedigering),
					)}
				</DollyFieldArrayWrapper>
				<RedigeringKnapp
					handleRedigering={() => handleRedigering(meldinger[meldinger.length - 1])}
				/>
			</HendelseKort>
		)
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
					<DollyFieldArrayWrapper>
						{hendelser.map(renderHendelse)}
						<div className="flexbox--flex-wrap">
							{meldingstyper.map(({ key, header, initialValues }) => (
								<FieldArrayAddButton
									key={key}
									addEntryButtonText={header}
									onClick={() =>
										fieldArrays[key].append({
											...initialValues,
											publiseringstidspunkt: nyHendelseTidspunkt(),
										})
									}
								/>
							))}
						</div>
					</DollyFieldArrayWrapper>
				</ErrorBoundary>
			</Panel>
		</Vis>
	)
}

KdiForm.validation = validation
