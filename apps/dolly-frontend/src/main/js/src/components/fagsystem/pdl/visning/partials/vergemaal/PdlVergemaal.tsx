import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { VergemaalKodeverk } from '~/config/kodeverk'
import { ArrayHistorikk } from '~/components/ui/historikk/ArrayHistorikk'
import { VergemaalData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { Verge } from './Verge'

type VergemaalProps = {
	data: Array<VergemaalData>
}

type VisningData = {
	data: VergemaalData
}

export const Visning = ({ data }: VisningData) => {
	const harFullmektig = data.type.includes('fullmakt')
	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue
						title="Fylkesmannsembete"
						kodeverk={VergemaalKodeverk.Fylkesmannsembeter}
						value={data.embete}
					/>
					<TitleValue title="Sakstype" kodeverk={VergemaalKodeverk.Sakstype} value={data.type} />
					<TitleValue
						title="Mandattype"
						kodeverk={VergemaalKodeverk.Mandattype}
						value={data.vergeEllerFullmektig?.omfang}
					/>
					<TitleValue
						title="Gyldig f.o.m."
						value={Formatters.formatDate(data.folkeregistermetadata?.gyldighetstidspunkt)}
					/>
					<TitleValue
						title="Gyldig t.o.m."
						value={Formatters.formatDate(data.folkeregistermetadata?.opphoerstidspunkt)}
					/>
					<Verge data={data.vergeEllerFullmektig} harFullmektig={harFullmektig} />
				</div>
			</ErrorBoundary>
		</>
	)
}

export const PdlVergemaal = ({ data }: VergemaalProps) => {
	if (!data || data.length < 1) {
		return null
	}

	const gyldigeVergemaal = data.filter((vergemaal: VergemaalData) => !vergemaal.metadata?.historisk)
	const historiskeVergemaal = data.filter(
		(vergemaal: VergemaalData) => vergemaal.metadata?.historisk
	)

	return (
		<div>
			<SubOverskrift label="Vergemål" iconKind="vergemaal" />
			<ArrayHistorikk
				component={Visning}
				data={gyldigeVergemaal}
				historiskData={historiskeVergemaal}
				header={''}
			/>
		</div>
	)
}
