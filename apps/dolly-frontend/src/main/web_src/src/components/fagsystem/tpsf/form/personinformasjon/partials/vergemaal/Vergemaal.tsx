import * as React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { VergemaalKodeverk } from '~/config/kodeverk'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const Vergemaal = () => {
	const paths = {
		embete: 'tpsf.vergemaal.embete',
		sakType: 'tpsf.vergemaal.sakType',
		mandatType: 'tpsf.vergemaal.mandatType',
		vedtakDato: 'tpsf.vergemaal.vedtakDato',
		identType: 'tpsf.vergemaal.identType',
		harMellomnavn: 'tpsf.vergemaal.harMellomnavn'
	}

	return (
		<Vis attributt={Object.values(paths)} formik>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name="tpsf.vergemaal.embete"
					label="Fylkesmannsembete"
					kodeverk={VergemaalKodeverk.Fylkesmannsembeter}
					size="xlarge"
					isClearable={false}
					visHvisAvhuket
				/>
				<FormikSelect
					name="tpsf.vergemaal.sakType"
					label="Sakstype"
					kodeverk={VergemaalKodeverk.Sakstype}
					size="xlarge"
					optionHeight={50}
					isClearable={false}
					visHvisAvhuket
				/>
				<FormikSelect
					name="tpsf.vergemaal.mandatType"
					label="Mandattype"
					kodeverk={VergemaalKodeverk.Mandattype}
					size="xxlarge"
					optionHeight={50}
					visHvisAvhuket
				/>
				<FormikDatepicker name={'tpsf.vergemaal.vedtakDato'} label="Vedtaksdato" />
				<FormikSelect
					name="tpsf.vergemaal.identType"
					label="Verges identtype"
					options={Options('identtype')}
					visHvisAvhuket
				/>
				<FormikSelect
					name="tpsf.vergemaal.harMellomnavn"
					label="Verge har mellomnavn"
					options={Options('boolean')}
					visHvisAvhuket
				/>
			</div>
		</Vis>
	)
}
