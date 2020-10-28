import React from 'react'
import { VergemaalKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { Historikk } from '~/components/ui/historikk/Historikk'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type Data = {
	data: VergemaalData
}

type DataListe = {
	data: Array<VergemaalData>
}

type VergemaalData = {
	embete: string
	mandatType?: string
	sakType: string
	vedtakDato: string
	verge: Verge
}

type Verge = {
	fornavn: string
	mellomnavn?: string
	etternavn: string
	ident: string
	identtype: string
	kjonn: string
}

export const Visning = ({ data }: Data) => {
	const verge = data.verge
	return (
		<>
			<div className="person-visning_content">
				<ErrorBoundary>
					<TitleValue
						title="Fylkesmannsembete"
						kodeverk={VergemaalKodeverk.Fylkesmannsembeter}
						value={data.embete}
					/>
					<TitleValue title="Sakstype" kodeverk={VergemaalKodeverk.Sakstype} value={data.sakType} />
					<TitleValue
						title="Mandattype"
						kodeverk={VergemaalKodeverk.Mandattype}
						value={data.mandatType}
					/>
					<TitleValue title="Vedtaksdato" value={Formatters.formatDate(data.vedtakDato)} />
				</ErrorBoundary>
			</div>
			<h4 style={{ marginTop: '0px' }}>Verge</h4>
			<div className="person-visning_content">
				<TitleValue title={verge.identtype} value={verge.ident} />
				<TitleValue title="Fornavn" value={verge.fornavn} />
				<TitleValue title="Mellomnavn" value={verge.mellomnavn} />
				<TitleValue title="Etternavn" value={verge.etternavn} />
				<TitleValue title="Kjønn" value={Formatters.kjonnToString(verge.kjonn)} />
			</div>
		</>
	)
}

export const Vergemaal = ({ data }: DataListe) => {
	if (!data || data.length < 1) return null
	return (
		<div>
			<SubOverskrift label="Vergemål" iconKind="vergemaal" />
			{/* @ts-ignore */}
			<Historikk component={Visning} data={data} />
		</div>
	)
}
