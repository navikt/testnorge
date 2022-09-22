import * as React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ArbeidKodeverk, GtKodeverk } from '~/config/kodeverk'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { useFormikContext } from 'formik'
import _get from 'lodash/get'
import { landkodeIsoMapping } from '~/service/services/kontoregister/landkoder'

const path = 'bankkonto.utenlandskBankkonto'

export const UtenlandskBankkonto = ({ formikBag }: any) => {
	const { values } = useFormikContext()
	const disableKontonummer = _get(values, `${path}.tilfeldigKontonummer`)
	const disableTilfeldigKontonummer = _get(values, `${path}.kontonummer`)

	const updateSwiftLandkode = (selected: any) => {
		if (!selected) {
			return
		}
		const landkode = selected.value
		// @ts-ignore
		const isoLandkode = landkodeIsoMapping[landkode]
		if (isoLandkode) {
			const mappedLandkode = isoLandkode ? isoLandkode : landkode.substring(0, 2)
			let swift = _get(values, `${path}.swift`)
			if (swift) {
				swift = swift.substring(0, 4) + mappedLandkode + swift.substring(6)
			} else {
				swift = 'BANK' + mappedLandkode + '11222'
			}
			formikBag.setFieldValue(`${path}.swift`, swift, false)
		}
	}

	return (
		<Vis attributt={path} formik>
			<div className="flexbox--flex-wrap">
				<div className="flexbox--flex-wrap">
					<FormikTextInput
						name={`${path}.kontonummer`}
						label={'Kontonummer'}
						disabled={disableKontonummer}
					/>
					<div style={{ marginTop: '31px' }}>
						<FormikCheckbox
							name={`${path}.tilfeldigKontonummer`}
							label="Har tilfeldig kontonummer"
							size="x-small"
							disabled={disableTilfeldigKontonummer}
						/>
					</div>
				</div>
				<div className="flexbox--flex-wrap">
					<FormikTextInput
						name={`${path}.swift`}
						label={'Swift kode'}
						size={'small'}
						useControlled={true}
					/>
					<FormikSelect
						name={`${path}.landkode`}
						label={'Land'}
						kodeverk={GtKodeverk.LAND}
						size={'large'}
						afterChange={updateSwiftLandkode}
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
