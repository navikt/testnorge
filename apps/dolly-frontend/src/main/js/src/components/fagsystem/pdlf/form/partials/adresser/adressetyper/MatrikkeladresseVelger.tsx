import { initialMatrikkeladresse } from '@/components/fagsystem/pdlf/form/initialValues'
import {
	Matrikkeladresse,
	MatrikkeladresseTilfeldig,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { Radio, RadioGroup } from '@navikt/ds-react'

export const MatrikkeladresseVelger = ({ formMethods, path }) => {
	const matrikkeladresseValg = {
		TILFELDIG: 'TILFELDIG',
		DETALJERT: 'DETALJERT',
	}

	const matrikkeladresseType = formMethods.watch(`${path}.matrikkeladresseType`) || null

	const handleRadioChange = (valg) => {
		formMethods.setValue(path, {
			...initialMatrikkeladresse,
			matrikkeladresseType: valg,
		})
		formMethods.trigger(path)
	}

	return (
		<div
			className="flexbox--full-width"
			key={`matrikkeladresse_${path}`}
			style={{ marginBottom: '10px' }}
		>
			<RadioGroup
				name={`matrikkeladresse_${path}`}
				size={'small'}
				key={`matrikkeladresse_${path}`}
				legend="Hva slags matrikkeladresse vil du opprette?"
				onChange={(valg) => handleRadioChange(valg)}
			>
				<Radio id={`tilfeldig_matrikkel_${path}`} value={matrikkeladresseValg.TILFELDIG}>
					Tilfeldig matrikkeladresse ...
				</Radio>
				<Radio id={`detaljert_matrikkel_${path}`} value={matrikkeladresseValg.DETALJERT}>
					Detaljert matrikkeladresse ...
				</Radio>
			</RadioGroup>

			{matrikkeladresseType === matrikkeladresseValg.TILFELDIG && (
				<MatrikkeladresseTilfeldig formMethods={formMethods} path={path} />
			)}

			{matrikkeladresseType === matrikkeladresseValg.DETALJERT && (
				<Matrikkeladresse formMethods={formMethods} path={path} />
			)}
		</div>
	)
}
