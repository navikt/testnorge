import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from "~/components/ui/form/inputs/datepicker/Datepicker";

export const YtelseForm = ({path, header, initialYtelser, formikBag }: any) => {
    return (
        <FormikDollyFieldArray name={path} header={header} newEntry={initialYtelser} nested>
            {(path: any, idx: React.Key) => (
                <React.Fragment key={idx}>
                    <FormikSelect
                        name={`${path}.type`}
                        label="Ytelse"
                        size="grow"
                        isClearable={false}
                        optionHeight={50}
                        options={Options('tjenestepensjonYtelseType')}
                    />
                    <FormikDatepicker name={`${path}.datoInnmeldtYtelseFom`} label="Medlemskap FOM" />
                    <FormikDatepicker name={`${path}.datoYtelseIverksattFom`} label="Ytelse FOM" />
                    <FormikDatepicker name={`${path}.datoYtelseIverksattTom`} label="Ytelse TOM" />
                </React.Fragment>
            )}
        </FormikDollyFieldArray>
    )
}
