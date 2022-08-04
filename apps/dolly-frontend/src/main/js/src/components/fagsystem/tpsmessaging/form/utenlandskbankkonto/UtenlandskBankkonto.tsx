import * as React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ArbeidKodeverk, GtKodeverk } from '~/config/kodeverk'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikCheckbox } from "~/components/ui/form/inputs/checbox/Checkbox";
import { useFormikContext } from "formik";
import _get from "lodash/get";

const path = 'bankkonto.utenlandskBankkonto'

export const UtenlandskBankkonto = () => {
	const { values } = useFormikContext()
	const disableKontonummer = _get(values, `${path}.tilfeldigKontonummer`)
	const disableTilfedligKontonummer = _get(values, `${path}.kontonummer`)

	return (
		<Vis attributt={path} formik>
			<div className="flexbox--flex-wrap">
				<div className="flexbox--flex-wrap">
					<FormikTextInput name={`${path}.kontonummer`} label={'Kontonummer'} disabled={disableKontonummer} />
					<div style={{marginTop: '31px'}}>
						<FormikCheckbox
							name={`${path}.tilfeldigKontonummer`}
							label="Har Tilfeldig kontonummer"
							size="x-small"
							disabled={disableTilfedligKontonummer}
						/>
					</div>
				</div>
				<div className="flexbox--flex-wrap">
					<FormikTextInput name={`${path}.swift`} label={'Swift kode'} size={'small'} />
					<FormikSelect
						name={`${path}.landkode`}
						label={'Land'}
						kodeverk={GtKodeverk.LAND}
						size={'large'}
					/>
					<FormikTextInput name={`${path}.banknavn`} label={'Banknavn'} size={'small'} />
					<FormikTextInput name={`${path}.iban`} label={'Bankkode'} />
					<FormikSelect
						name={`${path}.valuta`}
						label="Valuta"
						kodeverk={ArbeidKodeverk.Valutaer}
						size={'large'}
					/>
				</div>
				<div className="flexbox--flex-wrap">
					<FormikTextInput name={`${path}.bankAdresse1`} label={'Adresselinje 1'} />
					<FormikTextInput name={`${path}.bankAdresse2`} label={'Adresselinje 2'} />
					<FormikTextInput name={`${path}.bankAdresse3`} label={'Adresselinje 3'} />
				</div>
			</div>
		</Vis>
	)
}
