import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import React from 'react'
import { erGyldig } from '~/components/transaksjonid/GyldigeBestillinger'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

const RelasjonVisning = ({ relasjon, idx }) => {
	return (
		<div className="person-visning_content" key={idx}>
			<TitleValue
				title="Samboer f.o.m. dato"
				value={Formatters.formatDate(relasjon.samboerFraDato)}
			/>
			<TitleValue title="Dødsdato" value={Formatters.formatDate(relasjon.dodsdato)} />
			<TitleValue
				title="Er varig adskilt"
				value={Formatters.oversettBoolean(relasjon.varigAdskilt)}
			/>
			<TitleValue title="FNR" value={relasjon.fnr} />
			<TitleValue
				title="Dato for samlivsbrudd"
				value={Formatters.formatDate(relasjon.samlivsbruddDato)}
			/>
			<TitleValue title="Har vært gift" value={Formatters.oversettBoolean(relasjon.harVaertGift)} />
			<TitleValue
				title="Har felles barn"
				value={Formatters.oversettBoolean(relasjon.harFellesBarn)}
			/>
			<TitleValue title="Sum pensjonsinntekt" value={relasjon.sumAvForvArbKapPenInntekt} />
			<TitleValue
				title="Relasjonstype"
				kodeverk={PersoninformasjonKodeverk.Sivilstander}
				value={relasjon.relasjonType}
			/>
		</div>
	)
}
const DataVisning = ({ apBestilling, isArray = false }) => {
	return (
		<>
			<div className="person-visning_content">
				<TitleValue
					title="Iverksettelsesdato"
					value={Formatters.formatDate(apBestilling.iverksettelsesdato)}
				/>
				<TitleValue title="Uttaksgrad" value={apBestilling.uttaksgrad} />
				<TitleValue
					title="Sivilstand"
					kodeverk={PersoninformasjonKodeverk.Sivilstander}
					value={apBestilling.sivilstand}
				/>
				<TitleValue
					title="Sivilstand f.o.m. dato"
					value={Formatters.formatDate(apBestilling.sivilstatusDatoFom)}
				/>
			</div>
			{apBestilling.relasjonListe.length > 0 && (
				<>
					<h4>Relasjoner</h4>
					<div className="person-visning_content">
						<DollyFieldArray data={apBestilling.relasjonListe} nested whiteBackground={isArray}>
							{(relasjon, idx) => <RelasjonVisning relasjon={relasjon} idx={idx} />}
						</DollyFieldArray>
					</div>
				</>
			)}
		</>
	)
}

export const AlderspensjonVisning = ({ data }) => {
	// Det er ikke mulig aa hente tilbake AP-data, derfor viser vi kun bestillingsdata paa person
	if (!data || data.length < 1) {
		return null
	}

	const bestillingerFiltrert = data.filter((bestilling) => !bestilling.erGjenopprettet)
	if (bestillingerFiltrert?.length < 1) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Alderspensjon (PESYS)" iconKind="pensjon" />
			{bestillingerFiltrert?.length === 1 ? (
				<DataVisning
					apBestilling={bestillingerFiltrert[0]?.data?.pensjonforvalter?.alderspensjon}
				/>
			) : (
				<DollyFieldArray data={bestillingerFiltrert} nested>
					{(bestilling) => (
						<DataVisning apBestilling={bestilling.data?.pensjonforvalter?.alderspensjon} isArray />
					)}
				</DollyFieldArray>
			)}
		</>
	)
}

AlderspensjonVisning.filterValues = (bestillinger, ident) => {
	if (!bestillinger) {
		return null
	}
	return bestillinger.filter(
		(bestilling: any) =>
			bestilling.data.pensjonforvalter?.alderspensjon && erGyldig(bestilling.id, 'PEN_AP', ident)
	)
}
