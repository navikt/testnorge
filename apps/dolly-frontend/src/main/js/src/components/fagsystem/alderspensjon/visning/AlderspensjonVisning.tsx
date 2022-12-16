import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import React from 'react'
import { erGyldig } from '~/components/transaksjonid/GyldigeBestillinger'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

const DataVisning = ({ apBestilling }) => {
	return (
		<>
			<div className="person-visning_content">
				<TitleValue
					title="Iverksettelsesdato"
					value={Formatters.formatDate(apBestilling.iverksettelsesdato)}
				/>
				<TitleValue title="Uttaksgrad" value={`${apBestilling.uttaksgrad}%`} />
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
		</>
	)
}

export const AlderspensjonVisning = ({ data }) => {
	// Det er ikke mulig a hente tilbake AP-data, derfor viser vi kun bestillingsdata pa person
	if (!data || data.length < 1) {
		return null
	}

	const bestillingerFiltrert = data.filter((bestilling) => !bestilling.erGjenopprettet)
	if (bestillingerFiltrert?.length < 1) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Alderspensjon" iconKind="pensjon" />
			{bestillingerFiltrert?.length === 1 ? (
				<DataVisning
					apBestilling={bestillingerFiltrert[0]?.data?.pensjonforvalter?.alderspensjon}
				/>
			) : (
				<DollyFieldArray data={bestillingerFiltrert} nested>
					{(bestilling) => (
						<DataVisning apBestilling={bestilling.data?.pensjonforvalter?.alderspensjon} />
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
