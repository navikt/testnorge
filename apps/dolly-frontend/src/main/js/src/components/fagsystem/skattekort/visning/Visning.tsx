import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Alert } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, formatDate } from '@/utils/DataFormatter'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { useSkattekortKodeverk } from '@/utils/hooks/useSkattekort'
import { TrekktypeVisning } from '@/components/fagsystem/skattekort/visning/TrekktypeVisning'

type SkattekortVisning = {
	liste?: Array<SkattekortData>
	loading?: boolean
}

type SkattekortData = any

export const showKodeverkLabel = (kodeverkstype: any, value: any) => {
	const { kodeverk, loading, error } = useSkattekortKodeverk(kodeverkstype)
	if (loading || error) {
		return value
	}
	return kodeverk?.find((kode) => kode?.value === value)?.label || value
}

export const SkattekortVisning = ({ liste, loading }: SkattekortVisning) => {
	if (loading) {
		return <Loading label="Laster Inntektstub-data" />
	}
	if (!liste) {
		return null
	}

	const manglerFagsystemdata = liste?.length < 1

	return (
		<>
			<SubOverskrift label="Skattekort" iconKind="skattekort" isWarning={manglerFagsystemdata} />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke skattekort-data på person
				</Alert>
			) : (
				<ErrorBoundary>
					<DollyFieldArray header="" data={liste} expandable={liste.length > 5} nested>
						{(skattekort: SkattekortData) => {
							const arbeidsgiver = skattekort?.arbeidsgiver?.[0]
							const arbeidstaker = arbeidsgiver?.arbeidstaker?.[0]
							const trekkListe = arbeidstaker?.skattekort?.trekktype

							const tilleggsopplysningFormatted = arbeidstaker?.tilleggsopplysning?.map(
								(tilleggsopplysning) => {
									return showKodeverkLabel('TILLEGGSOPPLYSNING', tilleggsopplysning)
								},
							)

							return (
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
									<TrekktypeVisning trekkliste={trekkListe} />
								</div>
							)
						}}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
		</>
	)
}
