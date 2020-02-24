import React from 'react'
import _get from 'lodash/get'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import Formatters from '~/utils/DataFormatter'
import {FormikTextInput} from "~/components/ui/form/inputs/textInput/TextInput";

const initialValues = {
    inntektsperiode: {
        fom: new Date().getFullYear(),
        tom: null
    },
    beloep: ''
}

export const InntektForm = ({ formikBag }) => {
    const infotekst = 'Beløp angis som årsbeløp i dagens kroneverdi, og vil nedjusteres basert på snitt grunnbeløp i innteksåret.'

    return (
        <FormikDollyFieldArray name="popp" title="Pensjonsgivende inntekt" newEntry={initialValues}>
            {path => (
                <React.Fragment>
                    <React.Fragment>
                        <div className="flexbox--flex-wrap">
                            <FormikSelect
                                name={`${path}.inntektsperiode.fom`}
                                label="Fra og med år"
                                options={Formatters.getYearRangeOptions(
                                    1968,
                                    new Date().getFullYear()
                                )}
                                isClearable={false}
                            />
                            <FormikSelect
                                name={`${path}.inntektsperiode.tom`}
                                label="Til og med år"
                                options={Formatters.getYearRangeOptions(
                                    1968,
                                    new Date().getFullYear()
                                )}
                                isClearable={false}
                            />
                            <FormikTextInput
                                name={`${path}.beloep`}
                                label="Beløp"
                                type="number"
                                fastfield={false}
                                title={infotekst}
                            />
                        </div>
                    </React.Fragment>
                </React.Fragment>
            )}
        </FormikDollyFieldArray>
    )
}

