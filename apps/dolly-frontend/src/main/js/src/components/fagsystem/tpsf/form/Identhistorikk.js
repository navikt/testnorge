import React from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'

const initialValues = {
	foedtEtter: null,
	foedtFoer: null,
	identtype: null,
	kjonn: null,
	regdato: null
}

const hjelpetekst =
	'Dette er en oversikt over utgåtte identer, der de eldste skal ligge sist. ' +
	'Dagens identtype legges inn på forrige side. For å velge dagens kjønn må "Kjønn" hukes av på forrige side og velges under "Diverse" -> "Kjønn" her. ' +
	'Eksempel: En person med DNR får FNR. Da velges FNR på forrige side. DNR legges inn i denne oversikten. ' +
	'Hvis fødselsdatoen ble endret i overgangen kan født før og født etter fylles ut. Det samme gjelder for kjønn. ' +
	'Dersom de står som "Ikke spesifisert" beholdes samme fødselsdato og/eller kjønn.'

const identAttributt = 'tpsf.identHistorikk'

export const Identhistorikk = ({ formikBag }) => (
	<Vis attributt={identAttributt}>
		<Panel
			heading="Identhistorikk"
			hasErrors={panelError(formikBag, identAttributt)}
			informasjonstekst={hjelpetekst}
			iconType="identhistorikk"
			startOpen={() => erForste(formikBag.values, [identAttributt])}
		>
			<FormikDollyFieldArray name="tpsf.identHistorikk" header="Historikk" newEntry={initialValues}>
				{(path, idx) => (
					<React.Fragment key={idx}>
						<FormikSelect
							name={`${path}.identtype`}
							label="Identtype"
							options={Options('identtype')}
						/>
						<FormikSelect
							name={`${path}.kjonn`}
							label="Kjønn"
							kodeverk={PersoninformasjonKodeverk.Kjoennstyper}
						/>
						<FormikDatepicker name={`${path}.regdato`} label="Utgått dato" visHvisAvhuket={false} />
						<FormikDatepicker
							name={`${path}.foedtEtter`}
							label="Født etter"
							visHvisAvhuket={false}
						/>
						<FormikDatepicker name={`${path}.foedtFoer`} label="Født før" visHvisAvhuket={false} />
					</React.Fragment>
				)}
			</FormikDollyFieldArray>
		</Panel>
	</Vis>
)
