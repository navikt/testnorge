import React from 'react'

import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import {validation} from "~/components/fagsystem/pensjon/form/validation";
import {Kategori} from "~/components/ui/form/kategori/Kategori";
import {FormikSelect} from "~/components/ui/form/inputs/select/Select";
import Formatters from "~/utils/DataFormatter";
import {FormikTextInput} from "~/components/ui/form/inputs/textInput/TextInput";
import {FormikCheckbox} from "~/components/ui/form/inputs/checbox/Checkbox";

const pensjonAttributt = 'pensjonforvalter'
const path = `${pensjonAttributt}.inntekt`

export const PensjonForm = ({ formikBag }) => (
        <Vis attributt={pensjonAttributt}>
            <Panel
                heading="Pensjonsgivende inntekt"
                hasErrors={panelError(formikBag, pensjonAttributt)}
                iconType="sigrun"
                startOpen={() => erForste(formikBag.values, [pensjonAttributt])}
            >
                <Kategori title="Pensjonsgivende inntekt" vis={path}>
                    <React.Fragment>
                        <FormikSelect
                            name={`${path}.fomAar`}
                            label="Fra og med år"
                            options={Formatters.getYearRangeOptions(
                                1968,
                                new Date().getFullYear()
                            )}
                            isClearable={false}
                        />

                        <FormikSelect
                            name={`${path}.tomAar`}
                            label="Til og med år"
                            options={Formatters.getYearRangeOptions(
                                1968,
                                new Date().getFullYear()
                            )}
                            isClearable={false}
                        />
                        <FormikTextInput
                            name={`${path}.belop`}
                            label="Beløp"
                            type="number"
                            fastfield={false}
                        />

                        <FormikCheckbox
                            name={`${path}.redusertMedGrunnbelop`}
                            label="Nedjuster med grunnbeløp"
                            checkboxMargin
                        />
                    </React.Fragment>
                </Kategori>
            </Panel>
        </Vis>
)

PensjonForm.validation = validation