import React, { useState } from 'react'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import Button from '~/components/ui/button/Button'

export const VelgAdresseForm = ({ adresser, velgAdresse }) => {
	const mappedAdresser = adresser.map(mapAdresserToOptions)
	const [valgtAdresse, setValgtAdresse] = useState(mappedAdresser[0])
	const [valgtHusnummer, setValgtHusnummer] = useState(mappedAdresser[0].husnrfra)

	const onVelgAdresse = () => velgAdresse(valgtAdresse, valgtHusnummer)

	const handleChangeAdress = adresse => {
		setValgtAdresse(adresse)
		setValgtHusnummer(adresse.husnrfra)
	}

	return (
		<div>
			<h4>Velg adresse</h4>
			<div className="flexbox">
				<DollySelect
					name="adresse"
					label="Gyldig adresse"
					size="grow"
					options={mappedAdresser}
					value={valgtAdresse.value}
					onChange={handleChangeAdress}
					isClearable={false}
				/>
				<DollySelect
					name="husnummer"
					label="Husnummer"
					options={husnummerListe(valgtAdresse)}
					value={parseInt(valgtHusnummer)}
					onChange={v => setValgtHusnummer(v.value)}
					isClearable={false}
				/>
			</div>
			<Button onClick={onVelgAdresse}>Velg adresse</Button>
		</div>
	)
}

const mapAdresserToOptions = (adresse, idx) => ({
	...adresse,
	...(adresse.husnrfra === '' && { husnrfra: 1 }),
	label: `${adresse.adrnavn}, ${adresse.pnr} ${adresse.psted}`,
	value: idx
})

// Lage liste over husnr
const husnummerListe = adresse => {
	const liste = []
	for (var i = adresse.husnrfra; i <= adresse.husnrtil; i++) {
		liste.push({
			label: String(parseInt(i)),
			value: parseInt(i)
		})
	}
	return liste
}
