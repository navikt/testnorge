import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Attributt, AttributtKategori } from '../Attributt'

export const KontaktReservasjonsPanel = ({ stateModifier }) => {
	const sm = stateModifier(KontaktReservasjonsPanel.initialValues)

	const infoTekst = `KRR - benyttes for offentlige virksomheter for å avklare om den enkelte bruker har reservert seg mot digital kommunikasjon eller ikke.
	I tillegg skal varslene som sendes til bruker benytte den kontaktinformasjonen som ligger i registeret.
	Dette kan enten være mobiltelefonnummer for utsendelse av sms, eller epostadresse for utsendelse av epost. 
	Sikker digital postkasse kan settes på den enkelte bruker og inneholder foreløpig mulighet for Digipost og E-boks`

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
		label: 'Tilpass standard kontakt- og reservasjonsopplysninger',
		checked: has('krrstub'),
		add() {
			set('krrstub', {
				epost: '',
				gyldigFra: null,
				mobil: '',
				sdpAdresse: '',
				sdpLeverandoer: '',
				spraak: '',
				registrert: null,
				reservert: null
			})
		},
		remove() {
			del('krrstub')
		}
	}
})
