import React from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect, DollySelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import Panel from '~/components/ui/panel/Panel'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

const krrAttributt = 'krrstub'

export const KrrstubForm = ({ formikBag }) => {
	const leverandoerer = SelectOptionsOppslag.hentKrrLeverandoerer()
	const leverandoerOptions = SelectOptionsOppslag.formatOptions('sdpLeverandoer', leverandoerer)
	const registrert = _get(formikBag, 'values.krrstub.registrert')
	const infotekst = 'For å sette disse verdiene må personen være registrert i KRR'

	const handleRegistrertChange = registrert => {
		if (!registrert.value) {
			formikBag.setFieldValue('krrstub', {
				epost: '',
				gyldigFra: null,
				mobil: '',
				sdpAdresse: '',
				sdpLeverandoer: '',
				spraak: '',
				registrert: registrert.value,
				reservert: null
			})
		} else {
			formikBag.setFieldValue('krrstub.registrert', true)
		}
	}

	return (
		<Vis attributt={krrAttributt}>
			<Panel
				heading="Kontakt- og reservasjonsregisteret"
				hasErrors={panelError(formikBag, krrAttributt)}
				iconType="krr"
				startOpen={() => erForste(formikBag.values, [krrAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<DollySelect
						name="krrstub.registrert"
						label="Registrert i KRR"
						options={Options('boolean')}
						onChange={handleRegistrertChange}
						value={_get(formikBag.values, 'krrstub.registrert')}
						isClearable={false}
					/>
				</div>
				<div className="flexbox--flex-wrap" title={!registrert ? infotekst : null}>
					<FormikTextInput name="krrstub.epost" label="E-post" disabled={!registrert} />
					<FormikTextInput
						name="krrstub.mobil"
						label="Mobilnummer"
						type="number"
						disabled={!registrert}
					/>
					<FormikSelect
						name="krrstub.spraak"
						label="Språk"
						options={Options('spraaktype')}
						disabled={!registrert}
						fastfield={false}
					/>
					<FormikSelect
						name="krrstub.reservert"
						label="Reservert"
						options={Options('boolean')}
						disabled={!registrert}
						fastfield={false}
					/>
					<FormikDatepicker
						name="krrstub.gyldigFra"
						label="Kontaktinfo gjelder fra"
						disabled={!registrert}
						fastfield={false}
					/>
				</div>
				<div className="flexbox--flex-wrap" title={!registrert ? infotekst : null}>
					<Kategori title={'Sikker digital postkasse'}>
						<FormikTextInput name="krrstub.sdpAdresse" label="Adresse" disabled={!registrert} />
						<FormikSelect
							fastfield={false}
							name="krrstub.sdpLeverandoer"
							label="Leverandør"
							options={leverandoerOptions}
							disabled={!registrert}
						/>
					</Kategori>
				</div>
			</Panel>
		</Vis>
	)
}

KrrstubForm.validation = {
	krrstub: Yup.object({
		epost: '',
		gyldigFra: Yup.date().nullable(),
		mobil: Yup.string().matches(/^[0-9]*$/, 'Ugyldig mobilnummer'),
		sdpAdresse: '',
		sdpLeverandoer: '',
		spraak: '',
		registrert: '',
		reservert: ''
	})
}
