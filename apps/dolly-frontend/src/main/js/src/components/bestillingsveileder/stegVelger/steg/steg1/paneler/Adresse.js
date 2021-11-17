import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { boadressePaths } from '~/components/fagsystem/tpsf/form/adresser/Adresser'
import { Attributt, AttributtKategori } from '../Attributt'
import { initialUtenlandskAdresse } from '~/components/fagsystem/pdlf/form/initialValues'

export const AdressePanel = ({ stateModifier }) => {
	const sm = stateModifier(AdressePanel.initialValues)

	return (
		<Panel
			heading={AdressePanel.heading}
			checkAttributeArray={sm.batchAdd}
			uncheckAttributeArray={sm.batchRemove}
			iconType="adresse"
		>
			<AttributtKategori title="Boadresse">
				<Attributt attr={sm.attrs.boadresse} />
				<Attributt attr={sm.attrs.utenlandskAdresse} />
			</AttributtKategori>

			<AttributtKategori title="Postadresse">
				<Attributt attr={sm.attrs.postadresse} />
			</AttributtKategori>

			<AttributtKategori title="Midlertidig adresse">
				<Attributt attr={sm.attrs.midlertidigAdresse} />
			</AttributtKategori>
		</Panel>
	)
}

AdressePanel.heading = 'Adresser'

AdressePanel.initialValues = ({ set, setMulti, del, has }) => ({
	boadresse: {
		label: 'Har boadresse',
		checked: boadressePaths.some((path) => has(path)),
		add() {
			setMulti(['tpsf.boadresse', { flyttedato: null, bolignr: '' }], ['tpsf.adresseNrInfo', null])
		},
		remove() {
			del(['tpsf.boadresse', 'tpsf.adresseNrInfo'])
		},
	},
	utenlandskAdresse: {
		label: 'Har utenlandsk boadresse',
		checked: has('pdldata.person.bostedsadresse'),
		add: () =>
			setMulti([
				'pdldata.person.bostedsadresse',
				[
					{
						utenlandskAdresse: initialUtenlandskAdresse,
						kilde: 'Dolly',
						master: 'PDL',
						gjeldende: true,
					},
				],
				['tpsf.harIngenAdresse'],
				true,
			]),
		remove: () => del(['pdldata.person.bostedsadresse', 'tpsf.harIngenAdresse']),
	},
	postadresse: {
		label: 'Har postadresse',
		checked: has('tpsf.postadresse'),
		add: () =>
			set('tpsf.postadresse', [
				{
					postLand: 'NOR',
					postLinje1: '',
					postLinje2: '',
					postLinje3: '',
				},
			]),
		remove: () => del('tpsf.postadresse'),
	},
	midlertidigAdresse: {
		label: 'Har midlertidig adresse',
		checked: has('tpsf.midlertidigAdresse'),
		add: () =>
			set('tpsf.midlertidigAdresse', {
				adressetype: 'GATE',
				gyldigTom: '',
			}),
		remove: () => del('tpsf.midlertidigAdresse'),
	},
})
