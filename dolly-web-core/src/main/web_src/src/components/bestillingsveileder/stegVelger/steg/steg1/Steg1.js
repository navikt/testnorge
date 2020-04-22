import React, { useContext } from 'react'
import { AttributtVelger } from './attributtVelger/AttributtVelger'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { PersoninformasjonPanel } from './paneler/Personinformasjon'
import { AdressePanel } from './paneler/Adresse'
import { IdentifikasjonPanel } from './paneler/Identifikasjon'
import { FamilierelasjonPanel } from './paneler/Familierelasjoner'
import { ArbeidInntektPanel } from './paneler/ArbeidInntekt'
import { InstitusjonsoppholdPanel } from './paneler/Institusjonsopphold'
import { KontaktReservasjonsPanel } from './paneler/KontaktReservasjon'
import { KontaktDoedsboPanel } from './paneler/KontaktDoedsbo'
import { ArenaPanel } from './paneler/Arena'
import { UdiPanel } from './paneler/Udi'

export const Steg1 = ({ stateModifier }) => {
	const opts = useContext(BestillingsveilederContext)

	const checked = [
		PersoninformasjonPanel,
		AdressePanel,
		FamilierelasjonPanel,
		ArbeidInntektPanel,
		IdentifikasjonPanel,
		KontaktDoedsboPanel,
		InstitusjonsoppholdPanel,
		KontaktReservasjonsPanel,
		ArenaPanel,
		UdiPanel
	]
		.map(panel => ({
			label: panel.heading,
			values: stateModifier(panel.initialValues).checked
		}))
		.filter(v => v.values.length)

	return (
		<AttributtVelger checked={checked}>
			{!opts.is.leggTil && <PersoninformasjonPanel stateModifier={stateModifier} />}
			<AdressePanel stateModifier={stateModifier} />
			<FamilierelasjonPanel stateModifier={stateModifier} />
			<ArbeidInntektPanel stateModifier={stateModifier} />
			<IdentifikasjonPanel stateModifier={stateModifier} />
			<KontaktDoedsboPanel stateModifier={stateModifier} />
			<InstitusjonsoppholdPanel stateModifier={stateModifier} />
			<KontaktReservasjonsPanel stateModifier={stateModifier} />
			<ArenaPanel stateModifier={stateModifier} />
			{/* Vi kan foreløpig kun legge til UDI-attributter på personer som ikke har noen fra før */}
			{!opts.is.leggTil || !opts.data.udistub ? (
				<UdiPanel stateModifier={stateModifier} />
			) : (
				<>
					<AlertStripeInfo>
						<b>UDI</b>
						<p>
							Det er foreløpig ikke mulig å legge til UDI-attributter på personer som allerede har
							dette.
						</p>
					</AlertStripeInfo>
					<p />
				</>
			)}

			{opts.is.leggTil && (
				<AlertStripeInfo>
					<b>Funksjonen er under utvikling</b>
					<p>
						Det er foreløpig redusert støtte for å legge til attributter på person. Flere
						attributter vil bli lagt til fortløpende.
					</p>
				</AlertStripeInfo>
			)}
		</AttributtVelger>
	)
}

Steg1.label = 'Velg egenskaper'
