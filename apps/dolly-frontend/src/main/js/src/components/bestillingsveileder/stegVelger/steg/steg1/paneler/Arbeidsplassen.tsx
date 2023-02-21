export const ArbeidsplassenPanel = ({stateModifier, formikBag}) => {

}

ArbeidsplassenPanel.heading = 'Arbeidsplassen (CV)'

ArbeidsplassenPanel.initialValues = ({set, del, has}) => ({
    arbeidsplassen: {
        label: 'Har CV',
        checked: has('arbeidsplassen'),
        add: () => set('arbeidsplassen', [])
    }
})