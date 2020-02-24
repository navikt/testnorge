import React from 'react'

import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { InntektForm } from './partials/inntektForm'
import {validation} from "~/components/fagsystem/popp/form/validation";

const poppAttributt = 'popp'
export const POPPForm = ({ formikBag }) => (
    <Vis attributt={poppAttributt}>
        <Panel
            heading="Pensjonsgivende inntekt"
            hasErrors={panelError(formikBag,poppAttributt)}
            iconType="sigrun"
            startOpen={() => erForste(formikBag.values, [poppAttributt])}
        >
            <InntektForm formikBag={formikBag} />
        </Panel>
    </Vis>
)

POPPForm.validation = validation