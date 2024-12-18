import Loading from '@/components/ui/loading/Loading'
import React, { lazy, Suspense } from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Alert } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, formatDate, formatXml } from '@/utils/DataFormatter'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { useSkattekortKodeverk } from '@/utils/hooks/useSkattekort'
import { ForskuddstrekkVisning } from '@/components/fagsystem/skattekort/visning/ForskuddstrekkVisning'
import Button from '@/components/ui/button/Button'
import useBoolean from '@/utils/hooks/useBoolean'

type SkattekortVisning = {
	liste?: Array<any>
	loading?: boolean
}

const PrettyCode = lazy(() => import('@/components/codeView/PrettyCode'))

export const showKodeverkLabel = (kodeverkstype: string, value: string) => {
	const { kodeverk, loading, error } = useSkattekortKodeverk(kodeverkstype)
	if (loading || error) {
		return value
	}
	return kodeverk?.find((kode: any) => kode?.value === value)?.label || value
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
							const [viserXml, vis, skjul] = useBoolean(false)

							const arbeidsgiver = skattekort?.arbeidsgiver?.[0]
							const arbeidstaker = arbeidsgiver?.arbeidstaker?.[0]
							const trekkListe = arbeidstaker?.skattekort?.forskuddstrekk

							const tilleggsopplysningFormatted = arbeidstaker?.tilleggsopplysning?.map(
								(tilleggsopplysning: string) => {
									return showKodeverkLabel('TILLEGGSOPPLYSNING', tilleggsopplysning)
								},
							)

							const xmlFormatted = formatXml(skattekort?.skattekortXml, '  ')

							return (
								<React.Fragment key={idx}>
									<div className="person-visning_content">
										<TitleValue
											title="Resultat på forespørsel"
											value={showKodeverkLabel(
												'RESULTATSTATUS',
												arbeidstaker?.resultatPaaForespoersel,
											)}
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
										<TitleValue
											title="Tilleggsopplysning"
											value={arrayToString(tilleggsopplysningFormatted)}
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
									<Button
										onClick={viserXml ? skjul : vis}
										kind={viserXml ? 'chevron-up' : 'chevron-down'}
										style={{ position: 'initial', paddingTop: '0' }}
									>
										{(viserXml ? 'SKJUL ' : 'VIS ') + 'SKATTEKORT-XML'}
									</Button>
									{viserXml &&
										(skattekort?.skattekortXml ? (
											<Suspense fallback={<Loading label={'Laster xml...'} />}>
												<PrettyCode language={'xml'} codeString={xmlFormatted} wrapLongLines />
											</Suspense>
										) : (
											<Alert variant="error" size="small" inline>
												Kunne ikke vise skattekort-xml
											</Alert>
										))}
								</React.Fragment>
							)
						}}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
		</>
	)
}
