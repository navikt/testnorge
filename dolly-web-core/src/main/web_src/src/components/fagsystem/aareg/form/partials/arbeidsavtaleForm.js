import React, { useState } from 'react'
import _get from 'lodash/get'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import HjelpeTekst from 'nav-frontend-hjelpetekst'

const avtaltArbeidsTimerPerUke = 'avtaltArbeidsTimerPerUke'
const antallKonverterteTimer = 'antallKonverterteTimer'

export const ArbeidsavtaleForm = ({ formikBag, path }) => {
	const arbeidsavtalePath = `${path}.arbeidsavtale`
	const arbeidsavtale = _get(formikBag.values, arbeidsavtalePath)

	const [visning, setVisning] = useState(avtaltArbeidsTimerPerUke)

	const byttVisning = event => setVisning(event.target.value)

	arbeidsavtale.avtaltArbeidstimerPerUke != '' && visning === 'avtaltArbeidsTimerPerUke'
		? (arbeidsavtale.antallKonverterteTimer = '')
		: (arbeidsavtale.avtaltArbeidstimerPerUke = '')

	arbeidsavtale.antallKonverterteTimer != '' && visning === 'antallKonverterteTimer'
		? (arbeidsavtale.avtaltArbeidstimerPerUke = '')
		: (arbeidsavtale.antallKonverterteTimer = '')

	return (
		<div>
			<h3>Arbeidsavtale</h3>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name={`${arbeidsavtalePath}.yrke`}
					label="Yrke"
					kodeverk="Yrker"
					size="xxlarge"
					isClearable={false}
				/>
				<FormikTextInput
					name={`${arbeidsavtalePath}.stillingsprosent`}
					label="Stillingsprosent"
					type="number"
				/>
				<FormikDatepicker
					name={`${arbeidsavtalePath}.endringsdatoStillingsprosent`}
					label="Endringsdato stillingsprosent"
				/>
				<FormikSelect
					name={`${arbeidsavtalePath}.arbeidstidsordning`}
					label="Arbeidstidsordning"
					kodeverk="Arbeidstidsordninger"
					size="xxlarge"
					isClearable={false}
				/>
				<div className="toggle--wrapper">
					<ToggleGruppe onChange={byttVisning} name="timerToggle">
						<ToggleKnapp
							value={avtaltArbeidsTimerPerUke}
							checked={visning === avtaltArbeidsTimerPerUke}
						>
							Avtalte timer per uke
						</ToggleKnapp>
						<ToggleKnapp
							value={antallKonverterteTimer}
							checked={visning === antallKonverterteTimer}
						>
							Antall konverterte timer
						</ToggleKnapp>
					</ToggleGruppe>
				</div>
				<HjelpeTekst>
					Antall konverterte timer og avtalte timer per uke kan ikke være satt samtidig. Ved å bytte
				</HjelpeTekst>
				<FormikTextInput
					name={
						visning === 'avtaltArbeidsTimerPerUke'
							? `${arbeidsavtalePath}.avtaltArbeidstimerPerUke`
							: `${arbeidsavtalePath}.antallKonverterteTimer`
					}
					size="xxlarge"
					type="number"
					isclearable="true"
				/>
			</div>
		</div>
	)
}
