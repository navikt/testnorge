import Loading from '@/components/ui/loading/Loading'
import React, { lazy, Suspense } from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Alert } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, formatXml } from '@/utils/DataFormatter'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { useSkattekortKodeverk } from '@/utils/hooks/useSkattekort'
import { ForskuddstrekkVisning } from '@/components/fagsystem/skattekort/visning/ForskuddstrekkVisning'
import Button from '@/components/ui/button/Button'
import useBoolean from '@/utils/hooks/useBoolean'

type SkattekortVisning = {
	liste?: Array<any>
	loading?: boolean
}

type KodeverkTypes = {
	kodeverkstype: string
	value: string | string[]
	label: string
}

const PrettyCode = lazy(() => import('@/components/codeView/PrettyCode'))

export const KodeverkTitleValue = ({ kodeverkstype, value, label }: KodeverkTypes) => {
	const { kodeverk, loading, error } = useSkattekortKodeverk(kodeverkstype)
	if (loading || error || !kodeverk) {
		return <TitleValue title={label} value={value} />
	}
	if (Array.isArray(value)) {
		const labels = value.map(
			(val) => kodeverk?.find((kode: any) => kode?.value === val)?.label || val,
		)
		const arrayString = labels?.join(', ')
		return <TitleValue title={label} value={arrayString} />
	}
	const visningValue = kodeverk?.find((kode: any) => kode?.value === value)?.label || value
	return <TitleValue title={label} value={visningValue} />
}

const XmlVisning = ({ xmlString }: { xmlString: string }) => {
	const [viserXml, vis, skjul] = useBoolean(false)

	if (!xmlString) {
		return null
	}

	const xmlFormatted = formatXml(xmlString, '  ')

	return (
		<>
			<Button
				onClick={viserXml ? skjul : vis}
				kind={viserXml ? 'chevron-up' : 'chevron-down'}
				style={{ position: 'initial', paddingTop: '0' }}
			>
				{(viserXml ? 'SKJUL ' : 'VIS ') + 'SKATTEKORT-XML'}
			</Button>
			{viserXml &&
				(xmlString ? (
					<Suspense fallback={<Loading label={'Laster xml...'} />}>
						<PrettyCode language={'xml'} codeString={xmlFormatted} wrapLongLines />
					</Suspense>
				) : (
					<Alert variant="error" size="small" inline>
						Kunne ikke vise skattekort-xml
					</Alert>
				))}
		</>
	)
}

export const SkattekortVisning = ({ liste, loading }: SkattekortVisning) => {
	if (loading) {
		return <Loading label="Laster skattekort-data" />
	}
	if (!liste) {
		return null
	}

	const manglerFagsystemdata = liste?.length < 1

	return (
		<>
			<SubOverskrift
				label="Skattekort (SOKOS)"
				iconKind="skattekort"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke skattekort-data på person
				</Alert>
			) : (
				<ErrorBoundary>
					<DollyFieldArray header="" data={liste} expandable={liste.length > 5} nested>
						{(skattekort: any, idx: number) => {
							const arbeidsgiver = skattekort?.arbeidsgiver?.[0]
							const arbeidstaker = arbeidsgiver?.arbeidstaker?.[0]
							const trekkListe = arbeidstaker?.skattekort?.forskuddstrekk

							return (
								<React.Fragment key={idx}>
									<div className="person-visning_content">
										<KodeverkTitleValue
											kodeverkstype="RESULTATSTATUS"
											value={arbeidstaker?.resultatPaaForespoersel}
											label="Resultat på forespørsel"
										/>
										<TitleValue title="Inntektsår" value={arbeidstaker?.inntektsaar} />
										<TitleValue
											title="Utstedt dato"
											value={formatDate(arbeidstaker?.skattekort?.utstedtDato)}
										/>
										<TitleValue
											title="Skattekortidentifikator"
											value={arbeidstaker?.skattekort?.skattekortidentifikator}
										/>
										<KodeverkTitleValue
											kodeverkstype="TILLEGGSOPPLYSNING"
											value={arbeidstaker?.tilleggsopplysning}
											label="Tilleggsopplysning"
										/>
										<TitleValue
											title="Arbeidsgiver (org.nr.)"
											value={arbeidsgiver?.arbeidsgiveridentifikator?.organisasjonsnummer}
										/>
										<TitleValue
											title="Arbeidsgiver (ident)"
											value={arbeidsgiver?.arbeidsgiveridentifikator?.personidentifikator}
										/>
										<ForskuddstrekkVisning trekkliste={trekkListe} />
									</div>
									<XmlVisning xmlString={skattekort?.skattekortXml} />
								</React.Fragment>
							)
						}}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
		</>
	)
}
