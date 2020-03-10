import React, { useContext } from 'react'
import { AttributtVelger } from './attributtVelger/AttributtVelger'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { PersoninformasjonPanel } from './paneler/Personinformasjon'
import { AdressePanel } from './paneler/Adresse'
import { IdentifikasjonPanel } from './paneler/Identifikasjon'
import { FamilierelasjonPanel } from './paneler/Familierelasjoner'
import { ArbeidInntektPanel } from './paneler/ArbeidInntekt'
import { InstutisjonsoppholdPanel } from './paneler/Instutisjonsopphold'
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
		InstutisjonsoppholdPanel,
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
			<PersoninformasjonPanel stateModifier={stateModifier} />
			<AdressePanel stateModifier={stateModifier} />

			{!opts.is.leggTil && (
				<React.Fragment>
					<FamilierelasjonPanel stateModifier={stateModifier} />
					<ArbeidInntektPanel stateModifier={stateModifier} />
					<IdentifikasjonPanel stateModifier={stateModifier} />
					<KontaktDoedsboPanel stateModifier={stateModifier} />
					<InstutisjonsoppholdPanel stateModifier={stateModifier} />
					<KontaktReservasjonsPanel stateModifier={stateModifier} />
					<ArenaPanel stateModifier={stateModifier} />
					<UdiPanel stateModifier={stateModifier} />
				</React.Fragment>
			)}

			{opts.is.leggTil && (
				<AlertStripeInfo>
					<b>Kun støtte for å legge til innvandret, utvandret og adresse</b>
					<p>
						Det er foreløpig redusert støtte for å legge til attributter på person. Flere
						attributter vil blir lagt til etterhvert.
					</p>
				</AlertStripeInfo>
			)}
		</AttributtVelger>
	)
}

Steg1.label = 'Velg egenskaper'
