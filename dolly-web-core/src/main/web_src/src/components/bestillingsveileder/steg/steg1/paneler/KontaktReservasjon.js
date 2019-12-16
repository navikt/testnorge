import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import {
	Attributt,
	AttributtKategori
} from '~/components/bestillingsveileder/AttributtVelger/Attributt'

export const KontaktReservasjonsPanel = ({ stateModifier }) => {
	const sm = stateModifier(KontaktReservasjonsPanel.initialValues)

	const infoTekst =
		'KRR - benyttes for offentlige virksomheter for å avklare om den enkelte bruker har reservert seg mot digital kommunikasjon eller ikke. I tillegg skal varslene som sendes til bruker benytte den kontaktinformasjonen som ligger i registeret. Dette kan enten være mobiltelefonnummer for utsendelse av sms, eller epostadresse for utsendelse av epost.'

	return (
		<Panel
			heading={KontaktReservasjonsPanel.heading}
			informasjonstekst={infoTekst}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="krr"
		>
			<AttributtKategori>
				<Attributt attr={sm.attrs.krrstub} />
			</AttributtKategori>
		</Panel>
	)
}

KontaktReservasjonsPanel.heading = 'Kontakt- og reservasjonsregisteret'

KontaktReservasjonsPanel.initialValues = ({ set, del, has }) => ({
	krrstub: {
		label: 'Har kontaktinformasjon',
		checked: has('krrstub'),
		add() {
			set('krrstub', {
				epost: '',
				gyldigFra: new Date(),
				mobil: '',
				registrert: true,
				reservert: false
			})
		},
		remove() {
			del('krrstub')
		}
	}
})
