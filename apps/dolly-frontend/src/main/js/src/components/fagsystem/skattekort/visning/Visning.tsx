import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Alert } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { useSkattekortKodeverk } from '@/utils/hooks/useSkattekort'
import { ForskuddstrekkVisning } from '@/components/fagsystem/skattekort/visning/ForskuddstrekkVisning'

interface ForskuddstrekkDTO {
	trekkode?: string
	frikortBeloep?: number
	tabell?: string
	prosentSats?: number
	antallMndForTrekk?: number
}

interface SkattekortDTO {
	utstedtDato?: string
	inntektsaar: number
	resultatForSkattekort: string
	forskuddstrekkList: ForskuddstrekkDTO[]
	tilleggsopplysningList?: string[]
}

interface SkattekortVisningProps {
	liste?: SkattekortDTO[]
	loading?: boolean
}

interface KodeverkTypesProps {
	kodeverkstype: string
	value: string | string[]
	label: string
}

export const KodeverkTitleValue = ({ kodeverkstype, value, label }: KodeverkTypesProps) => {
	const { kodeverk, loading, error } = useSkattekortKodeverk(kodeverkstype)

	if (loading || error || !kodeverk) {
		return <TitleValue title={label} value={value} />
	}

	if (Array.isArray(value)) {
		const labels = value.map(
			(val) =>
				kodeverk?.find((kode: { label: string; value: string }) => kode?.value === val)?.label ||
				val,
		)
		const arrayString = labels?.join(', ')
		return <TitleValue title={label} value={arrayString} />
	}

	const visningValue =
		kodeverk?.find((kode: { label: string; value: string }) => kode?.value === value)?.label ||
		value
	return <TitleValue title={label} value={visningValue} />
}

export const SkattekortVisning = ({ liste, loading }: SkattekortVisningProps) => {
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
				label="Nav skattekort"
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
						{(skattekort: SkattekortDTO, idx: number) => {
							return (
								<React.Fragment key={idx}>
									<div className="person-visning_content">
										<KodeverkTitleValue
											kodeverkstype="RESULTATSTATUS_FRA_SOKOS"
											value={skattekort?.resultatForSkattekort}
											label="Resultat for skattekort"
										/>
										<TitleValue title="Inntektsår" value={skattekort?.inntektsaar} />
										<TitleValue title="Utstedt dato" value={formatDate(skattekort?.utstedtDato)} />
										<KodeverkTitleValue
											kodeverkstype="TILLEGGSOPPLYSNING_FRA_SOKOS"
											value={skattekort?.tilleggsopplysningList}
											label="Tilleggsopplysning"
										/>
										<ForskuddstrekkVisning trekkliste={skattekort?.forskuddstrekkList} />
									</div>
								</React.Fragment>
							)
						}}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
		</>
	)
}
