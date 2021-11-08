import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Button from '~/components/ui/button/Button'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '~/config/kodeverk'

interface PdlPersonValues {
	path: string
	label: string
}

export const PdlPersonForm = ({ path, label }: PdlPersonValues) => {
	const [visPersonValg, setVisPersonValg, setSkjulPersonValg] = useBoolean(false)

	return (
		<div className="flexbox--full-width">
			{visPersonValg ? (
				<Button onClick={setSkjulPersonValg} kind={'collapse'}>
					SKJUL VALG FOR {label}
				</Button>
			) : (
				<Button onClick={setVisPersonValg} kind={'expand'}>
					VIS VALG FOR {label}
				</Button>
			)}
			{visPersonValg && (
				<div className={'flexbox--flex-wrap'} style={{ marginTop: '10px' }}>
					<FormikSelect
						name={`${path}.identtype`}
						label="Identtype"
						options={Options('identtype')}
					/>
					<FormikSelect name={`${path}.kjoenn`} label="Kjønn" options={Options('kjoenn')} />
					<FormikDatepicker name={`${path}.foedtEtter`} label="Født etter" />
					<FormikDatepicker name={`${path}.foedtFoer`} label="Født før" />
					<FormikTextInput name={`${path}.alder`} type="number" label="Alder" disabled={true} />
					<FormikSelect
						name={`${path}.statsborgerskapLandkode`}
						label="Statsborgerskap"
						kodeverk={AdresseKodeverk.StatsborgerskapLand}
						size="large"
					/>
					<FormikSelect
						name={`${path}.gradering`}
						label="Gradering"
						options={Options('gradering')}
					/>
					<FormikCheckbox name={`${path}.syntetisk`} label="Er syntetisk" />
					<FormikCheckbox name={`${path}.nyttNavn.hasMellomnavn`} label="Har mellomnavn" />
				</div>
			)}
		</div>
	)
}
