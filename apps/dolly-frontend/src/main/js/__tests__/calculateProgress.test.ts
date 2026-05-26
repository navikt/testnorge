import {describe, expect, it} from 'vitest'
import {
    calculateProgress,
    sortFagsystemer,
} from '@/components/bestilling/statusListe/BestillingProgresjon/fagsystemUtils'

describe('calculateProgress', () => {
    describe('single person (antallIdenter === 1)', () => {
        it('should show fagsystem progress when ordering 1 person', () => {
            const statusList = [
                {id: 'PDL', navn: 'PDL', statuser: [{melding: 'OK'}]},
                {id: 'SKATT', navn: 'Skatteetaten', statuser: [{melding: 'OK'}]},
                {id: 'ARENA', navn: 'Arena', statuser: [{melding: 'Info: Oppretter'}]},
                {id: 'INST', navn: 'INST', statuser: []},
            ]
            const result = calculateProgress({
                antallIdenter: 1,
                antallLevert: 0,
                erOrganisasjon: false,
                statusList,
                totalFagsystemer: statusList.length,
            })

            expect(result.percent).toBe(50)
            expect(result.text).toBe('2 av 4 steg fullført')
        })

        it('should return 10% minimum when no fagsystems are completed', () => {
            const statusList = [
                {id: 'PDL', navn: 'PDL', statuser: [{melding: 'Info: Oppretter'}]},
                {id: 'SKATT', navn: 'Skatteetaten', statuser: []},
            ]
            const result = calculateProgress({
                antallIdenter: 1,
                antallLevert: 0,
                erOrganisasjon: false,
                statusList,
                totalFagsystemer: statusList.length,
            })

            expect(result.percent).toBe(10)
            expect(result.text).toBe('0 av 2 steg fullført')
        })

        it('should return 100% when all fagsystems are completed', () => {
            const statusList = [
                {id: 'PDL', navn: 'PDL', statuser: [{melding: 'OK'}]},
                {id: 'SKATT', navn: 'Skatteetaten', statuser: [{melding: 'OK'}]},
                {id: 'ARENA', navn: 'Arena', statuser: [{melding: 'OK'}]},
            ]
            const result = calculateProgress({
                antallIdenter: 1,
                antallLevert: 0,
                erOrganisasjon: false,
                statusList,
                totalFagsystemer: statusList.length,
            })

            expect(result.percent).toBe(100)
            expect(result.text).toBe('3 av 3 steg fullført')
        })

        it('should treat fagsystems with empty statuser as in-progress', () => {
            const statusList = [
                {id: 'PDL', navn: 'PDL', statuser: []},
                {id: 'SKATT', navn: 'Skatteetaten', statuser: [{melding: 'OK'}]},
            ]
            const result = calculateProgress({
                antallIdenter: 1,
                antallLevert: 0,
                erOrganisasjon: false,
                statusList,
                totalFagsystemer: statusList.length,
            })

            expect(result.percent).toBe(50)
            expect(result.text).toBe('1 av 2 steg fullført')
        })

        it('should treat ADDING_TO_QUEUE, RUNNING, PENDING_COMPLETE as in-progress', () => {
            const statusList = [
                {id: 'EREG', navn: 'EREG', statuser: [{melding: 'ADDING_TO_QUEUE'}]},
                {id: 'EREG2', navn: 'EREG2', statuser: [{melding: 'RUNNING'}]},
                {id: 'EREG3', navn: 'EREG3', statuser: [{melding: 'PENDING_COMPLETE'}]},
                {id: 'PDL', navn: 'PDL', statuser: [{melding: 'OK'}]},
            ]
            const result = calculateProgress({
                antallIdenter: 1,
                antallLevert: 0,
                erOrganisasjon: false,
                statusList,
                totalFagsystemer: statusList.length,
            })

            expect(result.percent).toBe(25)
            expect(result.text).toBe('1 av 4 steg fullført')
        })

        it('should treat Deployer and Pågående as in-progress', () => {
            const statusList = [
                {id: 'A', navn: 'A', statuser: [{melding: 'Deployer'}]},
                {id: 'B', navn: 'B', statuser: [{melding: 'Pågående'}]},
                {id: 'C', navn: 'C', statuser: [{melding: 'OK'}]},
            ]
            const result = calculateProgress({
                antallIdenter: 1,
                antallLevert: 0,
                erOrganisasjon: false,
                statusList,
                totalFagsystemer: statusList.length,
            })

            expect(result.percent).toBeCloseTo(33.33, 1)
            expect(result.text).toBe('1 av 3 steg fullført')
        })

        it('should treat error statuses as completed', () => {
            const statusList = [
                {id: 'PDL', navn: 'PDL', statuser: [{melding: 'OK'}]},
                {id: 'SKATT', navn: 'Skatteetaten', statuser: [{melding: 'Feil: Noe gikk galt'}]},
            ]
            const result = calculateProgress({
                antallIdenter: 1,
                antallLevert: 0,
                erOrganisasjon: false,
                statusList,
                totalFagsystemer: statusList.length,
            })

            expect(result.percent).toBe(100)
            expect(result.text).toBe('2 av 2 steg fullført')
        })

        it('should use totalFagsystemer as denominator when larger than statusList', () => {
            const result = calculateProgress({
                antallIdenter: 1,
                antallLevert: 0,
                erOrganisasjon: false,
                statusList: [{id: 'PDL', navn: 'PDL', statuser: [{melding: 'OK'}]}],
                totalFagsystemer: 6,
            })

            expect(result.percent).toBeCloseTo(16.67, 1)
            expect(result.text).toBe('1 av 6 steg fullført')
        })

        it('should fall back to ident progress when totalFagsystemer is 0', () => {
            const result = calculateProgress({
                antallIdenter: 1,
                antallLevert: 0,
                erOrganisasjon: false,
                statusList: [],
                totalFagsystemer: 0,
            })

            expect(result.percent).toBe(10)
            expect(result.text).toBe('Opprettet 0 av 1')
        })
    })

    describe('multiple persons (antallIdenter > 1)', () => {
        it('should use ident-based progress for multiple persons', () => {
            const result = calculateProgress({
                antallIdenter: 5,
                antallLevert: 3,
                erOrganisasjon: false,
                statusList: [{id: 'PDL', navn: 'PDL', statuser: [{melding: 'OK'}]}],
                totalFagsystemer: 1,
            })

            expect(result.percent).toBe(40)
            expect(result.text).toBe('Opprettet 2 av 5')
        })

        it('should return 10% minimum when no identer are delivered', () => {
            const result = calculateProgress({
                antallIdenter: 5,
                antallLevert: 0,
                erOrganisasjon: false,
                statusList: [],
                totalFagsystemer: 0,
            })

            expect(result.percent).toBe(10)
            expect(result.text).toBe('Opprettet 0 av 5')
        })
    })

    describe('organisasjon bestilling', () => {
        it('should always use ident-based progress for org bestillinger', () => {
            const result = calculateProgress({
                antallIdenter: 1,
                antallLevert: 0,
                erOrganisasjon: true,
                statusList: [{id: 'EREG', navn: 'EREG', statuser: [{melding: 'OK'}]}],
                totalFagsystemer: 1,
            })

            expect(result.percent).toBe(10)
            expect(result.text).toBe('Opprettet 0 av 1')
        })
    })
})

describe('sortFagsystemer', () => {

    it('should return a shallow copy preserving backend order', () => {
        const statusList = [
            {id: 'AAREG', navn: 'Arbeidsregister (AAREG)', statuser: [{melding: 'OK'}]},
            {id: 'PDL_FORVALTER', navn: 'Opprett persondetaljer', statuser: [{melding: 'OK'}]},
            {id: 'PDL_ORDRE', navn: 'Ordre til PDL', statuser: [{melding: 'OK'}]},
        ]
        const result = sortFagsystemer(statusList)

        expect(result).toHaveLength(3)
        expect(result[0].id).toBe('AAREG')
        expect(result[1].id).toBe('PDL_FORVALTER')
        expect(result[2].id).toBe('PDL_ORDRE')
        expect(result).not.toBe(statusList)
    })
})
