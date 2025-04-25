import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { Alert } from '@navikt/ds-react'
import * as _ from 'lodash-es'
import { DollyFaBlokk, DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	kategoriKodeverk,
	tekniskNavnKodeverk,
} from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/GrunnlagArrayForm'

export const kodeverkKeyToLabel = (key) => {
	if (!key) {
		return null
	}

	const filteredLabel = key?.replace('pensjonsgivendeInntektAv', '')
	const defaultLabel = filteredLabel?.replace(/([A-Z])/g, ' $1')

	switch (key) {
		case 'grunnlag':
			return 'Grunnlag'
		case 'spesifisering':
			return 'Spesifisering'
		case 'svalbardGrunnlag':
			return 'Grunnlag Svalbard'
		case 'kildeskattPaaLoennGrunnlag':
			return 'Kildeskatt på lønnsgrunnlag'
		default:
			return defaultLabel
	}
}

const SummertSkattegrunnlagVisning = ({ summertSkattegrunnlag, idx }) => {
	if (!summertSkattegrunnlag) {
		return null
	}

	return Object.entries(summertSkattegrunnlag)
		?.sort(([, a], [, b]) => {
			if (_.isArray(a) && !_.isArray(b)) return 1 // Arrays last for better readability
			if (_.isArray(b) && !_.isArray(a)) return -1
			return 0
		})
		?.map(([key, value]) => {
			const label = kodeverkKeyToLabel(key)
			const erDato = !isNaN(Date.parse(value))

			if (_.isArray(value)) {
				return (
					value.length > 0 && (
						<>
							<DollyFaBlokk key={key + idx} idx={idx} header={label} number={idx}>
								<SummertSkattegrunnlagVisning summertSkattegrunnlag={value?.[0]} idx={idx} />
							</DollyFaBlokk>
						</>
					)
				)
			}
			if (erDato) {
				return <TitleValue title={label} value={formatDate(value)} key={key + idx} />
			}
			if (key === 'tekniskNavn') {
				return (
					<TitleValue title={label} value={value} key={key + idx} kodeverk={tekniskNavnKodeverk} />
				)
			}
			if (key === 'kategori') {
				return (
					<TitleValue title={label} value={value} key={key + idx} kodeverk={kategoriKodeverk} />
				)
			}
			return <TitleValue title={label} value={value} key={key + idx} />
		})
}

export const SigrunstubSummertSkattegrunnlagVisning = ({ data, loading }) => {
	if (loading) {
		return <Loading label="Laster sigrunstub-data" />
	}
	if (!data) {
		return null
	}
	const manglerFagsystemdata = data?.length < 1

	return (
		<>
			<SubOverskrift label="Summert skattegrunnlag (Sigrun)" iconKind="sigrun" />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke data for summert skattegrunnlag på person
				</Alert>
			) : (
				<ErrorBoundary>
					<div className="person-visning_content" style={{ marginTop: '-15px' }}>
						<DollyFieldArray data={data} header={`Skattegrunnlag`} nested>
							{(skattegrunnlag, idx) => (
								<React.Fragment key={idx}>
									<SummertSkattegrunnlagVisning summertSkattegrunnlag={skattegrunnlag} idx={idx} />
								</React.Fragment>
							)}
						</DollyFieldArray>
					</div>
				</ErrorBoundary>
			)}
		</>
	)
}
